package com.microservice.pointsalecost.testServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;
import com.microservice.pointsalecost.exceptions.PointOfSaleNotFoundException;
import com.microservice.pointsalecost.exceptions.RedisCacheMissException;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.PointOfSale;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import com.microservice.pointsalecost.services.Impl.PointOfSaleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PointOfSaleServiceImplTest {

    @Mock
    private PointOfSaleRepository pointOfSaleRepository;

    @Mock
    private PointOfSaleMapper pointOfSaleMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    PointOfSaleServiceImpl pointOfSaleService;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        pointOfSaleService = new PointOfSaleServiceImpl(pointOfSaleRepository, pointOfSaleMapper, redisTemplate);
    }

    @Test
    public void testGetAll() {
        PointOfSale pointOfSale1 = new PointOfSale();
        pointOfSale1.setId(1L);
        pointOfSale1.setActive(true);

        PointOfSale pointOfSale2 = new PointOfSale();
        pointOfSale2.setId(2L);
        pointOfSale2.setActive(true);

        when(hashOperations.values(anyString())).thenReturn(Arrays.asList(pointOfSale1, pointOfSale2));
        when(pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale1)).thenReturn(new PointOfSaleResponseDTO(1L, "POS1", true));
        when(pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale2)).thenReturn(new PointOfSaleResponseDTO(2L, "POS2", true));

        List<PointOfSaleResponseDTO> result = pointOfSaleService.getAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    public void testFindById() {
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setId(1L);
        pointOfSale.setActive(true);

        when(hashOperations.get(anyString(), anyString())).thenReturn(null);
        when(pointOfSaleRepository.findById(1L)).thenReturn(Optional.of(pointOfSale));
        when(pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale)).thenReturn(new PointOfSaleResponseDTO(1L, "POS1", true));

        PointOfSaleResponseDTO result = pointOfSaleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void testFindById_NotFound() {
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);
        when(pointOfSaleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PointOfSaleNotFoundException.class, () -> pointOfSaleService.findById(1L));
    }

    @Test
    public void testSave() {
        PointOfSaleRequestDTO requestDTO = new PointOfSaleRequestDTO("POS1");
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setId(1L);
        pointOfSale.setActive(true);

        when(pointOfSaleMapper.toEntity(requestDTO)).thenReturn(pointOfSale);
        when(pointOfSaleRepository.save(any(PointOfSale.class))).thenReturn(pointOfSale);
        when(pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale)).thenReturn(new PointOfSaleResponseDTO(1L, "POS1", true));

        PointOfSaleResponseDTO result = pointOfSaleService.save(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(hashOperations, times(1)).put(anyString(), anyString(), any(PointOfSale.class));
    }

    @Test
    public void testUpdate() {
        PointOfSaleUpdateDTO updateDTO = new PointOfSaleUpdateDTO(1L, "Updated POS");
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setId(1L);
        pointOfSale.setActive(true);

        when(pointOfSaleRepository.findById(1L)).thenReturn(Optional.of(pointOfSale));
        when(pointOfSaleRepository.save(any(PointOfSale.class))).thenReturn(pointOfSale);
        when(pointOfSaleMapper.toPointOfSaleResponseDTO(pointOfSale)).thenReturn(new PointOfSaleResponseDTO(1L, "Updated POS", true));

        PointOfSaleResponseDTO result = pointOfSaleService.update(updateDTO);

        assertNotNull(result);
        assertEquals("Updated POS", result.name());
        verify(hashOperations, times(1)).put(anyString(), anyString(), any(PointOfSale.class));
    }

    @Test
    public void testDelete() {
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setId(1L);
        pointOfSale.setActive(true);

        when(hashOperations.delete(anyString(), anyString())).thenReturn(1L);
        when(pointOfSaleRepository.findById(1L)).thenReturn(Optional.of(pointOfSale));

        pointOfSaleService.delete(1L);

        verify(pointOfSaleRepository, times(1)).save(pointOfSale);
        assertFalse(pointOfSale.isActive());
    }

    @Test
    public void testDelete_NotFoundInRedis() {
        when(hashOperations.delete(anyString(), anyString())).thenReturn(0L);

        assertThrows(RedisCacheMissException.class, () -> pointOfSaleService.delete(1L));
    }
}
