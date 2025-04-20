package com.microservice.pointsalecost.controllers;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;
import com.microservice.pointsalecost.exceptions.ApplicationException;
import com.microservice.pointsalecost.services.PointOfSaleService;
import com.microservice.pointsalecost.utils.RoleValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/pointOfSale")
@Tag(name = "Point Of Sale", description = "Endpoints for managing point of sale entities")
public class PontOfSaleController {

    @Autowired
    private PointOfSaleService pointOfSaleService;

    @PostMapping("/add")
    @Operation(summary = "Add Point of Sale", description = "Adds a new Point of Sale. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Point of Sale added successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid request")})
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> addPointOfSale(@RequestBody @Valid PointOfSaleRequestDTO pointOfSaleRequestDTO,
                                                                                 @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to add a Point of Sale", "ADMIN");
        PointOfSaleResponseDTO responseDTO = pointOfSaleService.save(pointOfSaleRequestDTO);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Point Of Sale Added", responseDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Point of Sale by ID", description = "Fetch a specific Point of Sale by its ID. ADMIN and CLIENT roles are allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point of Sale found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Point of Sale not found")})
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> getPointOfSale(@PathVariable Long id, @RequestHeader("X-User-Authorities") String roles) {

        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN or CLIENT to get a specific Point of Sale", "ADMIN", "CLIENT");
        PointOfSaleResponseDTO pointOfSale = pointOfSaleService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Point Of Sale Found", pointOfSale));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get All Points of Sale", description = "Returns a list of all registered Points of Sale. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Points of Sale returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")})
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> getAllPointOfSales(@RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to get all Point of Sale", "ADMIN");
        List<PointOfSaleResponseDTO> pointOfSaleResponse = pointOfSaleService.getAll();
        if(pointOfSaleResponse.isEmpty()) {
            return new ResponseEntity<>(new ApiResponseDTO<>(false, "Point Of Sales NOT FOUND", pointOfSaleResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponseDTO<>(true, "Point Of Sales FOUND", pointOfSaleResponse), HttpStatus.OK);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update Point of Sale", description = "Updates a Point of Sale. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point of Sale updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid request")})
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> updatePointOfSale(@RequestBody @Valid PointOfSaleUpdateDTO pointOfSaleUpdateDTO,
                                                                                    @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to update a Point of Sale", "ADMIN");
        PointOfSaleResponseDTO updatedPointOfSale = pointOfSaleService.update(pointOfSaleUpdateDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Point Of Sale Updated", updatedPointOfSale));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Point of Sale", description = "Deletes a Point of Sale by ID. Only ADMIN role is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point of Sale deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Point of Sale not found")})
    public ResponseEntity<?> deletePontOfSale(@PathVariable Long id, @RequestHeader("X-User-Authorities") String roles) {
        RoleValidator.validateRole(roles,"Access denied: You must be an ADMIN to delete a Point of Sale", "ADMIN");
        pointOfSaleService.delete(id);
        String message = "Point of Sale Successfully Deleted";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/internal/{id}")
    @Operation(summary = "Internal Get Point of Sale", description = "Fetches a Point of Sale by ID for internal use.")
    @ApiResponse(responseCode = "200", description = "Point of Sale found")
    public ResponseEntity<PointOfSaleResponseDTO> getPointOfSaleInternal(@Parameter(description = "Point of Sale ID") @PathVariable Long id) {
        PointOfSaleResponseDTO pointOfSale = pointOfSaleService.findById(id);
        return ResponseEntity.ok(pointOfSale);
    }
}