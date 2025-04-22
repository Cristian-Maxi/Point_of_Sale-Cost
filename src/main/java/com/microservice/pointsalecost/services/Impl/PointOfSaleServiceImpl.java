package com.microservice.pointsalecost.services.Impl;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.*;
import com.microservice.pointsalecost.enums.CacheType;
import com.microservice.pointsalecost.exceptions.PointOfSaleNotFoundException;
import com.microservice.pointsalecost.exceptions.RedisCacheMissException;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.PointOfSale;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import com.microservice.pointsalecost.services.PointOfSaleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PointOfSaleServiceImpl implements PointOfSaleService {

    private final PointOfSaleRepository pointOfSaleRepository;
    private final PointOfSaleMapper pointOfSaleMapper;
    private final HashOperations<String, String, PointOfSale> pointOfSaleHashOperations;

    public PointOfSaleServiceImpl(PointOfSaleRepository pointOfSaleRepository, PointOfSaleMapper pointOfSaleMapper, RedisTemplate<String, Object> redisTemplate) {
        this.pointOfSaleRepository = pointOfSaleRepository;
        this.pointOfSaleMapper = pointOfSaleMapper;
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
        if (Objects.isNull(pointOfSale)) {
            pointOfSale = pointOfSaleRepository.findById(id)
                    .orElseThrow(() -> new PointOfSaleNotFoundException("Point of Sale not found with ID: " + id));

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
            throw new RedisCacheMissException("Point of Sale ID not found in Redis: " + id);
        }

        PointOfSale pointOfSale = pointOfSaleRepository.findById(id)
                .orElseThrow(() -> new PointOfSaleNotFoundException("Point of Sale not found with ID: " + id));

        pointOfSale.setActive(false);
        pointOfSaleRepository.save(pointOfSale);
    }
}
