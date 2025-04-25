package com.microservice.pointsalecost.utils;

import com.microservice.pointsalecost.enums.CacheType;
import com.microservice.pointsalecost.models.Cost;
import com.microservice.pointsalecost.models.PointOfSale;
import com.microservice.pointsalecost.repositories.CostRepository;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheDataInitializer.class);

    private final PointOfSaleRepository pointOfSaleRepository;
    private final CostRepository costRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheDataInitializer(PointOfSaleRepository pointOfSaleRepository, CostRepository costRepository, RedisTemplate<String, Object> redisTemplate) {
        this.pointOfSaleRepository = pointOfSaleRepository;
        this.costRepository = costRepository;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public ApplicationRunner initializeRedisCache() {
        return args -> {
            initializePointOfSaleCache();
            initializeCostCache();
        };
    }

    private void initializePointOfSaleCache() {
        List<PointOfSale> pointOfSaleData = List.of(
                new PointOfSale("CABA", true),
                new PointOfSale("GBA_1", true),
                new PointOfSale("GBA_2", true),
                new PointOfSale("Santa Fe", true),
                new PointOfSale("Córdoba", true),
                new PointOfSale("Misiones", true),
                new PointOfSale("Salta", true),
                new PointOfSale("Chubut", true),
                new PointOfSale("Santa Cruz", true),
                new PointOfSale("Catamarca", true)
        );

        List<PointOfSale> savedPointOfSaleData;
        if (pointOfSaleRepository.count() == 0) {
            savedPointOfSaleData = pointOfSaleRepository.saveAll(pointOfSaleData);
            logger.info("Point of Sale saved in DB");
        } else {
            savedPointOfSaleData = pointOfSaleRepository.findAll();
            logger.info("Point of Sale already exists in DB");
        }

        HashOperations<String, String, PointOfSale> hashOps = redisTemplate.opsForHash();
        if (hashOps.size(CacheType.POINT_OF_SALE.getValues()) == 0) {
            savedPointOfSaleData.forEach(pos ->
                    hashOps.put(CacheType.POINT_OF_SALE.getValues(), pos.getId().toString(), pos));
            logger.info("Point of Sale Cache Initialized");
        } else {
            logger.info("Point of Sale Cache Already Initialized");
        }
    }

    private void initializeCostCache() {
        List<Cost> costData = List.of(
                new Cost(1L, 2L, 2d),
                new Cost(1L, 3L, 3d),
                new Cost(2L, 3L, 5d),
                new Cost(2L, 4L, 10d),
                new Cost(1L, 4L, 11d),
                new Cost(4L, 5L, 5d),
                new Cost(2L, 5L, 14d),
                new Cost(6L, 7L, 32d),
                new Cost(8L, 9L, 11d),
                new Cost(10L, 7L, 5d),
                new Cost(3L, 8L, 10d),
                new Cost(5L, 8L, 30d),
                new Cost(10L, 5L, 5d),
                new Cost(4L, 6L, 6d)
        );

        if (costRepository.count() == 0) {
            costRepository.saveAll(costData);
            logger.info("Cost data saved in DB");
        } else {
            logger.info("Cost data already exists in DB");
        }

        HashOperations<String, String, Cost> hashOps = redisTemplate.opsForHash();
        if (hashOps.size(CacheType.COST.getValues()) == 0) {
            costRepository.findAll().forEach(cost -> {
                String key = cost.getIdA() + "-" + cost.getIdB();
                hashOps.put(CacheType.COST.getValues(), key, cost);
            });
            logger.info("Cost Cache Initialized");
        } else {
            logger.info("Cost Cache Already Initialized");
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void cleanCache() {
        redisTemplate.delete(CacheType.POINT_OF_SALE.getValues());
        redisTemplate.delete(CacheType.COST.getValues());
        logger.info("Cleaning CACHE");
    }
}