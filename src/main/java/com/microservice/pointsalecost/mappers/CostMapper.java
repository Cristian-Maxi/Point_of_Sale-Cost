package com.microservice.pointsalecost.mappers;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.models.Cost;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CostMapper {
    CostMapper INSTANCE = Mappers.getMapper(CostMapper.class);

    CostResponseDTO toCostResponseDTO(Cost cost);

    Cost toEntity(CostRequestDTO costRequestDTO);
}
