package com.microservice.pointsalecost.mappers;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.models.PointOfSale;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointOfSaleMapper {
    PointOfSaleMapper INSTANCE = Mappers.getMapper(PointOfSaleMapper.class);

    PointOfSaleResponseDTO toPointOfSaleResponseDTO(PointOfSale pointOfSale);

    PointOfSale toEntity(PointOfSaleRequestDTO pointOfSaleRequestDTO);
}