package com.microservice.pointsalecost.testControllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.microservice.pointsalecost.controllers.CostController;
import com.microservice.pointsalecost.dtos.CostDTO.CostMinimumDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.services.CostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CostController.class)
public class CostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CostService costService;

    @InjectMocks
    private CostController costController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCost() throws Exception {
        CostRequestDTO requestDTO = new CostRequestDTO(1L, 2L, 15.50);
        CostResponseDTO responseDTO = new CostResponseDTO(1L, 2L, 15.50);

        when(costService.save(any(CostRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cost/add")
                        .header("X-User-Authorities", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idA\":1,\"idB\":2,\"amount\":15.50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.idA").value(1L))
                .andExpect(jsonPath("$.data.idB").value(2L))
                .andExpect(jsonPath("$.data.amount").value(15.50));
    }

    @Test
    public void testDeleteCost() throws Exception {
        mockMvc.perform(delete("/api/cost/delete/1/2")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Deleted cost between 1 and 2"));

        verify(costService, times(1)).delete(1L, 2L);
    }

    @Test
    public void testGetDirectCostsFromPoint() throws Exception {
        CostResponseDTO responseDTO1 = new CostResponseDTO(1L, 2L, 15.50);
        CostResponseDTO responseDTO2 = new CostResponseDTO(2L, 3L, 20.00);
        List<CostResponseDTO> responseList = Arrays.asList(responseDTO1, responseDTO2);

        when(costService.directCostFromOnePoint(1L)).thenReturn(responseList);

        mockMvc.perform(get("/api/cost/direct/1")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataIterable[0].idA").value(1L))
                .andExpect(jsonPath("$.dataIterable[0].idB").value(2L))
                .andExpect(jsonPath("$.dataIterable[0].amount").value(15.50))
                .andExpect(jsonPath("$.dataIterable[1].idA").value(2L))
                .andExpect(jsonPath("$.dataIterable[1].idB").value(3L))
                .andExpect(jsonPath("$.dataIterable[1].amount").value(20.00));
    }

    @Test
    public void testGetMinimumCostPath() throws Exception {
        CostMinimumDTO costMinimumDTO = new CostMinimumDTO(Arrays.asList(
                new PointOfSaleResponseDTO(1L, "Terminal A", true),
                new PointOfSaleResponseDTO(2L, "Terminal B", true)
        ), 15.50);

        when(costService.minimumCostPath(1L, 2L)).thenReturn(costMinimumDTO);

        mockMvc.perform(get("/api/cost/minimum/1/2")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pointOfSaleResponse[0].id").value(1L))
                .andExpect(jsonPath("$.data.pointOfSaleResponse[0].name").value("Terminal A"))
                .andExpect(jsonPath("$.data.pointOfSaleResponse[1].id").value(2L))
                .andExpect(jsonPath("$.data.pointOfSaleResponse[1].name").value("Terminal B"))
                .andExpect(jsonPath("$.data.totalCost").value(15.50));
    }

    @Test
    public void testGetMinimumCostPath_NotFound() throws Exception {
        when(costService.minimumCostPath(1L, 2L)).thenReturn(null);

        mockMvc.perform(get("/api/cost/minimum/1/2")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(false))
                .andExpect(jsonPath("$.message").value("No Minimum Cost Path Found"));
    }
}
