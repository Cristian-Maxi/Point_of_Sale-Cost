package com.microservice.pointsalecost.testControllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.microservice.pointsalecost.controllers.PontOfSaleController;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;
import com.microservice.pointsalecost.services.PointOfSaleService;
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

@WebMvcTest(PontOfSaleController.class)
public class PontOfSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointOfSaleService pointOfSaleService;

    @InjectMocks
    private PontOfSaleController pontOfSaleController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddPointOfSale() throws Exception {
        PointOfSaleRequestDTO requestDTO = new PointOfSaleRequestDTO("Terminal A");
        PointOfSaleResponseDTO responseDTO = new PointOfSaleResponseDTO(1L, "Terminal A", true);

        when(pointOfSaleService.save(any(PointOfSaleRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pointOfSale/add")
                        .header("X-User-Authorities", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Terminal A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Terminal A"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    public void testGetPointOfSale() throws Exception {
        PointOfSaleResponseDTO responseDTO = new PointOfSaleResponseDTO(1L, "Terminal A", true);

        when(pointOfSaleService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pointOfSale/1")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Terminal A"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    public void testGetAllPointOfSales() throws Exception {
        PointOfSaleResponseDTO responseDTO1 = new PointOfSaleResponseDTO(1L, "Terminal A", true);
        PointOfSaleResponseDTO responseDTO2 = new PointOfSaleResponseDTO(2L, "Terminal B", true);
        List<PointOfSaleResponseDTO> responseList = Arrays.asList(responseDTO1, responseDTO2);

        when(pointOfSaleService.getAll()).thenReturn(responseList);

        mockMvc.perform(get("/api/pointOfSale/getAll")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataIterable[0].id").value(1L))
                .andExpect(jsonPath("$.dataIterable[0].name").value("Terminal A"))
                .andExpect(jsonPath("$.dataIterable[0].active").value(true))
                .andExpect(jsonPath("$.dataIterable[1].id").value(2L))
                .andExpect(jsonPath("$.dataIterable[1].name").value("Terminal B"))
                .andExpect(jsonPath("$.dataIterable[1].active").value(true));
    }

    @Test
    public void testUpdatePointOfSale() throws Exception {
        PointOfSaleUpdateDTO updateDTO = new PointOfSaleUpdateDTO(1L, "Updated Terminal");
        PointOfSaleResponseDTO responseDTO = new PointOfSaleResponseDTO(1L, "Updated Terminal", true);

        when(pointOfSaleService.update(any(PointOfSaleUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/pointOfSale/update")
                        .header("X-User-Authorities", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Updated Terminal\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Updated Terminal"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    public void testDeletePointOfSale() throws Exception {
        mockMvc.perform(delete("/api/pointOfSale/delete/1")
                        .header("X-User-Authorities", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Point of Sale Successfully Deleted"));

        verify(pointOfSaleService, times(1)).delete(1L);
    }

    @Test
    public void testGetPointOfSaleInternal() throws Exception {
        PointOfSaleResponseDTO responseDTO = new PointOfSaleResponseDTO(1L, "Terminal A", true);

        when(pointOfSaleService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pointOfSale/internal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Terminal A"))
                .andExpect(jsonPath("$.active").value(true));
    }
}
