package com.microservice.pointsalecost.controllers;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import com.microservice.pointsalecost.dtos.CostDTO.*;
import com.microservice.pointsalecost.exceptions.ApplicationException;
import com.microservice.pointsalecost.services.CostService;
import com.microservice.pointsalecost.utils.RoleValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost")
@Tag(name = "Cost", description = "Endpoints for managing cost relationships between Points of Sale")
public class CostController {

    @Autowired
    private CostService costService;

    @PostMapping("/add")
    @Operation(summary = "Add Cost", description = "Adds a new cost relationship between two Points of Sale. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cost added successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid request")})
    public ResponseEntity<ApiResponseDTO<CostResponseDTO>> addCost(@RequestBody @Valid CostRequestDTO costRequestDTO,
                                                                   @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to add a cost", "ADMIN");
        CostResponseDTO costResponseDTO = costService.save(costRequestDTO);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Cost Added Successfully", costResponseDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{idA}/{idB}")
    @Operation(summary = "Delete Cost", description = "Deletes a cost relationship between two Points of Sale by their IDs. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cost deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cost relationship not found")})
    public ResponseEntity<ApiResponseDTO<String>> deleteCost(@PathVariable Long idA, @PathVariable Long idB,
                                                             @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to delete a cost", "ADMIN");
        costService.delete(idA, idB);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Cost Deleted Successfully", "Deleted cost between " + idA + " and " + idB), HttpStatus.OK);
    }

    @GetMapping("/direct/{id}")
    @Operation(summary = "Get Direct Costs", description = "Returns a list of direct costs from a specific Point of Sale. Accessible by ADMIN and CLIENT roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direct costs returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No direct costs found")})
    public ResponseEntity<ApiResponseDTO<CostResponseDTO>> getDirectCostsFromPoint(@PathVariable Long id,
                                                                                   @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN or CLIENT to delete a user", "ADMIN", "CLIENT");
        List<CostResponseDTO> costResponseList = costService.directCostFromOnePoint(id);
        boolean success = !costResponseList.isEmpty();
        String message = success ? "Direct Costs Found" : "No Direct Costs Found From Point " + id;
        return new ResponseEntity<>(new ApiResponseDTO<>(success, message, costResponseList), HttpStatus.OK);
    }

    @GetMapping("/minimum/{idA}/{idB}")
    @Operation(summary = "Get Minimum Cost Path", description = "Finds the path with the minimum cost between two Points of Sale. Accessible by ADMIN and CLIENT roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Minimum cost path returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No minimum cost path found")})
    public ResponseEntity<ApiResponseDTO<CostMinimumDTO>> getMinimumCostPath(@PathVariable Long idA, @PathVariable Long idB,
                                                                             @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN or CLIENT to get the minimum cost path", "ADMIN", "CLIENT");
        CostMinimumDTO costMinimumDTO = costService.minimumCostPath(idA, idB);
        if (costMinimumDTO == null || costMinimumDTO.pointOfSaleResponse().isEmpty()) {
            return new ResponseEntity<>(new ApiResponseDTO<>(false, "No Minimum Cost Path Found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Minimum Cost Path Found", costMinimumDTO), HttpStatus.OK);
    }
}