package com.microservice.pointsalecost.services;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;

import java.util.List;

public interface PointOfSaleService {

    List<PointOfSaleResponseDTO> getAll();
    PointOfSaleResponseDTO findById(Long id);
    PointOfSaleResponseDTO save(PointOfSaleRequestDTO pointOfSaleRequestDTO);
    PointOfSaleResponseDTO update(PointOfSaleUpdateDTO pointOfSaleUpdateDTO);
    void delete(Long id);
}