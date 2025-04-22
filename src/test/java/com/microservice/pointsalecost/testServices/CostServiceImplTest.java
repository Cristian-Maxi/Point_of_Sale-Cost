package com.microservice.pointsalecost.testServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.exceptions.CostAlreadyExistsException;
import com.microservice.pointsalecost.exceptions.CostNotFoundException;
import com.microservice.pointsalecost.exceptions.InvalidCostException;
import com.microservice.pointsalecost.exceptions.PointOfSaleNotFoundException;
import com.microservice.pointsalecost.mappers.CostMapper;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.Cost;
import com.microservice.pointsalecost.models.CostID;
import com.microservice.pointsalecost.repositories.CostRepository;
import com.microservice.pointsalecost.repositories.PointOfSaleRepository;
import com.microservice.pointsalecost.services.Impl.CostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CostServiceImplTest {

    @Mock
    private CostRepository costRepository;

    @Mock
    private PointOfSaleRepository pointOfSaleRepository;

    @Mock
    private CostMapper costMapper;

    @Mock
    private PointOfSaleMapper pointOfSaleMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> pointOfSaleHashOperations;

    @Mock
    private HashOperations<String, Object, Object> costHashOperations;

    CostServiceImpl costService;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(pointOfSaleHashOperations);
        when(redisTemplate.opsForHash()).thenReturn(costHashOperations);
        costService = new CostServiceImpl(costRepository, pointOfSaleRepository, costMapper, pointOfSaleMapper, redisTemplate);
    }

    @Test
    public void testSave() {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 2L, 15.50);
        Cost cost = new Cost();
        cost.setId(new CostID(1L, 2L));
        cost.setAmount(15.50);

        when(pointOfSaleRepository.existsById(1L)).thenReturn(true);
        when(pointOfSaleRepository.existsById(2L)).thenReturn(true);
        when(costMapper.toEntity(requestDTO)).thenReturn(cost);
        when(costRepository.save(any(Cost.class))).thenReturn(cost);
        when(costMapper.toCostResponseDTO(cost)).thenReturn(new CostResponseDTO(1L, 2L, 15.50));

        CostResponseDTO result = costService.save(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.idA());
        assertEquals(2L, result.idB());
        assertEquals(15.50, result.amount());
        verify(costHashOperations, times(1)).put(anyString(), anyString(), any(Cost.class));
    }

    @Test
    public void testSave_InvalidCost() {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 1L, 15.50);

        assertThrows(InvalidCostException.class, () -> costService.save(requestDTO));
    }

    @Test
    public void testSave_NegativeAmount() {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 2L, -15.50);

        assertThrows(InvalidCostException.class, () -> costService.save(requestDTO));
    }

    @Test
    public void testSave_PointOfSaleNotFound() {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 2L, 15.50);

        when(pointOfSaleRepository.existsById(1L)).thenReturn(false);
        when(pointOfSaleRepository.existsById(2L)).thenReturn(false);

        assertThrows(PointOfSaleNotFoundException.class, () -> costService.save(requestDTO));
    }

    @Test
    public void testSave_CostAlreadyExists() {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 2L, 15.50);

        when(pointOfSaleRepository.existsById(1L)).thenReturn(true);
        when(pointOfSaleRepository.existsById(2L)).thenReturn(true);
        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(true);

        assertThrows(CostAlreadyExistsException.class, () -> costService.save(requestDTO));
    }

    @Test
    public void testDelete() {
        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(true);

        costService.delete(1L, 2L);

        verify(costHashOperations, times(1)).delete(anyString(), anyString());
        verify(costRepository, times(1)).deleteById(any(CostID.class));
    }

    @Test
    public void testDelete_CostNotFound() {
        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(false);
        when(costRepository.existsById(any(CostID.class))).thenReturn(false);

        assertThrows(CostNotFoundException.class, () -> costService.delete(1L, 2L));
    }

    @Test
    public void testDirectCostFromOnePoint() {
        Cost cost1 = new Cost();
        cost1.setId(new CostID(1L, 2L));
        cost1.setAmount(15.50);

        Cost cost2 = new Cost();
        cost2.setId(new CostID(2L, 3L));
        cost2.setAmount(20.00);

        when(costHashOperations.entries(anyString())).thenReturn(Map.of("1-2", cost1, "2-3", cost2));
        when(costMapper.toCostResponseDTO(cost1)).thenReturn(new CostResponseDTO(1L, 2L, 15.50));
        //when(costMapper.toCostResponseDTO(cost2)).thenReturn(new CostResponseDTO(2L, 3L, 20.00));

        List<CostResponseDTO> result = costService.directCostFromOnePoint(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).idA());
        assertEquals(2L, result.get(0).idB());
        assertEquals(15.50, result.get(0).amount());
    }

    @Test
    public void testValidateCostNotExists_CostExistsInCache() {
        CostRequestDTO costRequestDTO = new CostRequestDTO(1L, 2L, 15.50);

        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(true);

        assertThrows(CostAlreadyExistsException.class, () -> costService.validateCostNotExists(costRequestDTO));
    }

    @Test
    public void testValidateCostNotExists_CostExistsInDatabase() {
        CostRequestDTO costRequestDTO = new CostRequestDTO(1L, 2L, 15.50);

        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(false);
        when(costRepository.existsById(any(CostID.class))).thenReturn(true);

        assertThrows(CostAlreadyExistsException.class, () -> costService.validateCostNotExists(costRequestDTO));
    }

    @Test
    public void testValidateCostNotExists_CostDoesNotExist() {
        CostRequestDTO costRequestDTO = new CostRequestDTO(1L, 2L, 15.50);

        when(costHashOperations.hasKey(anyString(), anyString())).thenReturn(false);
        when(costRepository.existsById(any(CostID.class))).thenReturn(false);

        costService.validateCostNotExists(costRequestDTO);

        verify(costHashOperations, times(1)).hasKey(anyString(), anyString());
        verify(costRepository, times(1)).existsById(any(CostID.class));
    }
}
