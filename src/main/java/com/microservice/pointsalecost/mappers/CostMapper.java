package com.microservice.pointsalecost.mappers;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.models.Cost;
import com.microservice.pointsalecost.models.CostID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CostMapper {
    CostMapper INSTANCE = Mappers.getMapper(CostMapper.class);

    @Mapping(target = "id", source = "costRequestDTO", qualifiedByName = "mapToCostID")
    Cost toEntity(CostRequestDTO costRequestDTO);

    @Mapping(target = "idA", expression = "java(cost.getId().getIdA())")
    @Mapping(target = "idB", expression = "java(cost.getId().getIdB())")
    CostResponseDTO toCostResponseDTO(Cost cost);

    @Named("mapToCostID")
    static CostID mapToCostID(CostRequestDTO dto) {
        return new CostID(dto.idA(), dto.idB());
    }
}
