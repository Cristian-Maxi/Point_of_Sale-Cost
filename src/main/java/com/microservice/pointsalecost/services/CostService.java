package com.microservice.pointsalecost.services;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostMinimumDTO;

import java.util.List;

public interface CostService {
    CostResponseDTO save(CostRequestDTO costRequestDTO);
    void delete(Long idA, Long idB);
    List<CostResponseDTO> directCostFromOnePoint(Long id);
    CostMinimumDTO minimumCostPath(Long idA, Long idB);
}