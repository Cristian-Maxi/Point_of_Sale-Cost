package com.microservice.pointsalecost.controllers;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import com.microservice.pointsalecost.dtos.CostDTO.*;
import com.microservice.pointsalecost.services.CostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    @Autowired
    private CostService costService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponseDTO<CostResponseDTO>> addCost(@RequestBody @Valid CostRequestDTO costRequestDTO) {
        CostResponseDTO costResponseDTO = costService.save(costRequestDTO);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Cost Added Successfully", costResponseDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{idA}/{idB}")
    public ResponseEntity<ApiResponseDTO<String>> deleteCost(@PathVariable Long idA, @PathVariable Long idB) {
        costService.delete(idA, idB);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Cost Deleted Successfully", "Deleted cost between " + idA + " and " + idB), HttpStatus.OK);
    }

    @GetMapping("/direct/{id}")
    public ResponseEntity<ApiResponseDTO<CostResponseDTO>> getDirectCostsFromPoint(@PathVariable Long id) {
        List<CostResponseDTO> costResponseList = costService.directCostFromOnePoint(id);
        boolean success = !costResponseList.isEmpty();
        String message = success ? "Direct Costs Found" : "No Direct Costs Found From Point " + id;
        return new ResponseEntity<>(new ApiResponseDTO<>(success, message, costResponseList), HttpStatus.OK);
    }

    @GetMapping("/minimum/{idA}/{idB}")
    public ResponseEntity<ApiResponseDTO<CostMinimumDTO>> getMinimumCostPath(@PathVariable Long idA, @PathVariable Long idB) {
        CostMinimumDTO costMinimumDTO = costService.minimumCostPath(idA, idB);
        if (costMinimumDTO == null || costMinimumDTO.pointOfSaleResponse().isEmpty()) {
            return new ResponseEntity<>(new ApiResponseDTO<>(false, "No Minimum Cost Path Found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Minimum Cost Path Found", costMinimumDTO), HttpStatus.OK);
    }
}