package com.microservice.pointsalecost.services.Impl;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.*;
import com.microservice.pointsalecost.enums.CacheType;
import com.microservice.pointsalecost.exceptions.ApplicationException;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.PointOfSale;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import com.microservice.pointsalecost.services.PointOfSaleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PointOfSaleServiceImpl implements PointOfSaleService {

    @Autowired
    PointOfSaleRepository pointOfSaleRepository;
    @Autowired
    PointOfSaleMapper pointOfSaleMapper;

    private final HashOperations<String, String, PointOfSale> pointOfSaleHashOperations;

    public PointOfSaleServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.pointOfSaleHashOperations = redisTemplate.opsForHash();
    }

    @Override
    public List<PointOfSaleResponseDTO> getAll() {
        List<PointOfSale> pointOfSaleList = pointOfSaleHashOperations.values(CacheType.POINT_OF_SALE.getValues());

        if (pointOfSaleList.isEmpty()) {
            pointOfSaleList = pointOfSaleRepository.findAll();
            for (PointOfSale pointOfSale : pointOfSaleList) {
                if (pointOfSale.isActive()) {
                    pointOfSaleHashOperations.put(CacheType.POINT_OF_SALE.getValues(), pointOfSale.getId().toString(), pointOfSale);
                }
            }
        }
        return pointOfSaleList.stream()
                .filter(PointOfSale::isActive)
                .sorted(Comparator.comparing(PointOfSale::getId))
                .map(pointOfSaleMapper::toPointOfSaleResponseDTO)
                .toList();
    }

    @Override
    public PointOfSaleResponseDTO findById(Long id) {
        PointOfSale pointOfSale = pointOfSaleHashOperations.get(CacheType.POINT_OF_SALE.getValues(), id.toString());
        if (pointOfSale == null) {
            pointOfSale = pointOfSaleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Point Of Sale not Found: " + id));

            if (pointOfSale.isActive()) {
                pointOfSaleHashOperations.put(CacheType.POINT_OF_SALE.getValues(), id.toString(), pointOfSale);
            }
        }
        return pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale);
    }

    @Transactional
    @Override
    public PointOfSaleResponseDTO save(PointOfSaleRequestDTO pointOfSaleRequestDTO) {
        PointOfSale pointOfSale = pointOfSaleMapper.toEntity(pointOfSaleRequestDTO);
        pointOfSale.setActive(true);
        pointOfSale = pointOfSaleRepository.save(pointOfSale);
        pointOfSaleHashOperations.put(CacheType.POINT_OF_SALE.getValues(), pointOfSale.getId().toString(), pointOfSale);
        return pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale);
    }

    @Transactional
    @Override
    public PointOfSaleResponseDTO update(PointOfSaleUpdateDTO pointOfSaleUpdateDTO) {
        PointOfSale pointOfSale = pointOfSaleRepository.findById(pointOfSaleUpdateDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("Point of Sale ID Not Found"));

        if(pointOfSaleUpdateDTO.name() != null && !pointOfSaleUpdateDTO.name().isBlank()) {
            pointOfSale.setName(pointOfSaleUpdateDTO.name());
        }

        pointOfSaleHashOperations.put(CacheType.POINT_OF_SALE.getValues(), pointOfSale.getId().toString(), pointOfSale);
        pointOfSaleRepository.save(pointOfSale);
        return pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Long deleteCache = pointOfSaleHashOperations.delete(CacheType.POINT_OF_SALE.getValues(), id.toString());

        if(deleteCache.equals(0L)) {
            throw new ApplicationException("Point of Sale ID Not Found in REDIS" + id);
        }

        PointOfSale pointOfSale = pointOfSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Point of Sale ID Not Found"));

        pointOfSale.setActive(false);
        pointOfSaleRepository.save(pointOfSale);
    }
}
