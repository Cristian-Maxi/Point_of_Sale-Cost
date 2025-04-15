package com.microservice.pointsalecost.services.Impl;

import com.microservice.pointsalecost.dtos.CostDTO.*;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.enums.CacheType;
import com.microservice.pointsalecost.exceptions.*;
import com.microservice.pointsalecost.mappers.CostMapper;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.Cost;
import com.microservice.pointsalecost.models.CostID;
import com.microservice.pointsalecost.models.PointOfSale;
import com.microservice.pointsalecost.repositories.CostRepository;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import com.microservice.pointsalecost.services.CostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private CostRepository costRepository;
    @Autowired
    private PointOfSaleRepository pointOfSaleRepository;
    @Autowired
    private CostMapper costMapper;
    @Autowired
    private PointOfSaleMapper pointOfSaleMapper;

    private final HashOperations<String, String, PointOfSale> pointOfSaleHashOperations;
    private final HashOperations<String, String, Cost> costHashOperations;

    public CostServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.pointOfSaleHashOperations = redisTemplate.opsForHash();
        this.costHashOperations = redisTemplate.opsForHash();
    }

    @Transactional
    @Override
    public CostResponseDTO save(CostRequestDTO costRequestDTO) {
        if (costRequestDTO.idA().equals(costRequestDTO.idB())) {
            throw new InvalidCostException("Cannot add a cost between a point and itself.");
        }

        if (costRequestDTO.amount() < 0) {
            throw new InvalidCostException("The amount cannot be negative.");
        }

        validatePointsExist(costRequestDTO);
        validateCostNotExists(costRequestDTO);

        CostID costId = new CostID(costRequestDTO.idA(), costRequestDTO.idB());
        Cost cost = costMapper.toEntity(costRequestDTO);
        cost.setId(costId);
        String key = keyGenerator(costRequestDTO.idA(), costRequestDTO.idB());
        costHashOperations.put(CacheType.COST.getValues(), key, cost);
        Cost saved = costRepository.save(cost);
        return costMapper.toCostResponseDTO(saved);
    }

    private void validatePointsExist(CostRequestDTO dto) {
        boolean existsA = pointOfSaleRepository.existsById(dto.idA());
        boolean existsB = pointOfSaleRepository.existsById(dto.idB());

        if (!existsA && !existsB) {
            throw new PointOfSaleNotFoundException("Point of Sale A or B not found: " + dto.idA() + " and " + dto.idB());
        } else if (!existsA) {
            throw new PointOfSaleNotFoundException("Point of Sale A not found: " + dto.idA());
        } else if (!existsB) {
            throw new PointOfSaleNotFoundException("Point of Sale B not found: " + dto.idB());
        }
    }

    private void validateCostNotExists(CostRequestDTO dto) {
        String key = keyGenerator(dto.idA(), dto.idB());

        if (costHashOperations.hasKey(CacheType.COST.getValues(), key)) {
            throw new CostAlreadyExistsException("The cost between these two points already exists in cache.");
        }

        CostID costId = new CostID(dto.idA(), dto.idB());
        if (costRepository.existsById(costId)) {
            throw new CostAlreadyExistsException("The cost between these two points already exists in database.");
        }
    }

    @Transactional
    @Override
    public void delete(Long idA, Long idB) {
        String key = keyGenerator(idA, idB);
        CostID costId = new CostID(idA, idB);

        if (costHashOperations.hasKey(CacheType.COST.getValues(), key)) {
            costHashOperations.delete(CacheType.COST.getValues(), key);
            costRepository.deleteById(costId);
        } else if (costRepository.existsById(costId)) {
            costRepository.deleteById(costId);
        } else {
            throw new CostNotFoundException("There is NO cost between " + idA + " and " + idB);
        }
    }

    @Override
    public List<CostResponseDTO> directCostFromOnePoint(Long id) {
        Map<String, Cost> costsInCache = costHashOperations.entries(CacheType.COST.getValues());

        if (costsInCache.isEmpty()) {
            List<Cost> costFromOnePoint = costRepository.findByIdAOrIdB(id, id);
            return costFromOnePoint.stream()
                    .map(costMapper::toCostResponseDTO)
                    .collect(Collectors.toList());
        } else {
            return costsInCache.values().stream()
                    .filter(cost -> cost.getId().getIdA().equals(id) || cost.getId().getIdB().equals(id))
                    .map(costMapper::toCostResponseDTO)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CostMinimumDTO minimumCostPath(Long idA, Long idB) {
        PointOfSale pointOfSale_A = obtainPointOfSale(idA);
        PointOfSale pointOfSale_B = obtainPointOfSale(idB);

        if (!isPointOfSaleValid(pointOfSale_A) || !isPointOfSaleValid(pointOfSale_B)) {
            throw new InactivePointOfSaleException("Points of sales are inactive or do not exist.");
        }

        Map<String, Cost> costsInCache = costHashOperations.entries(CacheType.COST.getValues());
        if (costsInCache.isEmpty()) {
            List<Cost> costsList = costRepository.findAll();
            costsInCache = new HashMap<>();
            for (Cost cost : costsList) {
                String key = keyGenerator(cost.getIdA(), cost.getIdB());
                costsInCache.put(key, cost);
            }
        }

        Map<Long, Map<Long, Double>> graphStructure = graphBuilder(costsInCache);
        return searchCostMinimumPath(graphStructure, idA, idB);
    }

    private boolean isPointOfSaleValid(PointOfSale pos) {
        return pos != null && pos.isActive();
    }

    private PointOfSale obtainPointOfSale(Long id) {
        String key = id.toString();
        return pointOfSaleHashOperations.hasKey(CacheType.POINT_OF_SALE.getValues(), key)
                ? pointOfSaleHashOperations.get(CacheType.POINT_OF_SALE.getValues(), key)
                : pointOfSaleRepository.findById(id).orElse(null);
    }

    private Map<Long, Map<Long, Double>> graphBuilder(Map<String, Cost> costs) {
        Map<Long, Map<Long, Double>> graphStructure = new HashMap<>();

        for (Cost cost : costs.values()) {
            PointOfSale pointOfSale_A = obtainPointOfSale(cost.getId().getIdA());
            PointOfSale pointOfSale_B = obtainPointOfSale(cost.getId().getIdB());

            if (pointOfSale_A != null && pointOfSale_B != null && pointOfSale_A.isActive() && pointOfSale_B.isActive()) {
                graphStructure.computeIfAbsent(cost.getId().getIdA(), k -> new HashMap<>()).put(cost.getId().getIdB(), cost.getAmount());
                graphStructure.computeIfAbsent(cost.getId().getIdB(), k -> new HashMap<>()).put(cost.getId().getIdA(), cost.getAmount());
            }
        }
        return graphStructure;
    }


    private CostMinimumDTO searchCostMinimumPath(Map<Long, Map<Long, Double>> graph, Long from, Long to) {
        PriorityQueue<Nodo> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.amount));
        //Minimum cost from the starting point to each node.
        Map<Long, Double> minimumCost = new HashMap<>();
        //To reconstruct the minimum path at the end.
        Map<Long, Long> minimumPath = new HashMap<>();

        queue.add(new Nodo(from, 0.0));
        minimumCost.put(from, 0.0);

        while (!queue.isEmpty()) {
            Nodo current = queue.poll();
            if (current.id.equals(to)) {
                return costMinimumPathResponse(to, minimumPath, minimumCost.get(to));
            }
            for (Map.Entry<Long, Double> nextPoint : graph.getOrDefault(current.id, new HashMap<>()).entrySet()) {
                PointOfSale nextPointOfSale;
                if (!pointOfSaleHashOperations.hasKey(CacheType.POINT_OF_SALE.getValues(), nextPoint.getKey().toString())) {
                    nextPointOfSale = pointOfSaleRepository.findById(nextPoint.getKey()).orElse(null);
                } else {
                    nextPointOfSale = pointOfSaleHashOperations.get(CacheType.POINT_OF_SALE.getValues(), nextPoint.getKey().toString());
                }

                if (nextPointOfSale != null && nextPointOfSale.isActive()) {
                    double newCost = current.amount + nextPoint.getValue();
                    if (newCost < minimumCost.getOrDefault(nextPoint.getKey(), Double.MAX_VALUE)) {
                        minimumCost.put(nextPoint.getKey(), newCost);
                        minimumPath.put(nextPoint.getKey(), current.id);
                        queue.add(new Nodo(nextPoint.getKey(), newCost));
                    }
                }
            }
        }

        return null;
    }

    private CostMinimumDTO costMinimumPathResponse(Long to, Map<Long, Long> previous, Double totalCost) {
        List<PointOfSaleResponseDTO> pointOfSaleList = new LinkedList<>();
        for (Long current = to; current != null; current = previous.get(current)) {
            PointOfSale pointOfSale = pointOfSaleHashOperations.get(CacheType.POINT_OF_SALE.getValues(), current.toString());
            pointOfSaleList.add(0, pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale)); // insert at beginning
        }
        return new CostMinimumDTO(pointOfSaleList, totalCost);
    }

    private record Nodo(Long id, Double amount) {}

    private static String keyGenerator(Long idA, Long idB) {
        return idA < idB ? idA + "-" + idB : idB + "-" + idA;
    }
}