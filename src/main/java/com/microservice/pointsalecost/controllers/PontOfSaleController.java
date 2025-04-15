package com.microservice.pointsalecost.controllers;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;
import com.microservice.pointsalecost.exceptions.ApplicationException;
import com.microservice.pointsalecost.services.PointOfSaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointOfSale")
public class PontOfSaleController {

    @Autowired
    private PointOfSaleService pointOfSaleService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> addPointOfSale(@RequestBody @Valid PointOfSaleRequestDTO pointOfSaleRequestDTO) {
        PointOfSaleResponseDTO responseDTO = pointOfSaleService.save(pointOfSaleRequestDTO);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Point Of Sale Added", responseDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> getPointOfSale(@PathVariable Long id) {
        PointOfSaleResponseDTO pointOfSale = pointOfSaleService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Point Of Sale Found", pointOfSale));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> getAllPointOfSales() {
        List<PointOfSaleResponseDTO> pointOfSaleResponse = pointOfSaleService.getAll();
        if(pointOfSaleResponse.isEmpty()) {
            return new ResponseEntity<>(new ApiResponseDTO<>(false, "Point Of Sales NOT FOUND", pointOfSaleResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponseDTO<>(true, "Point Of Sales FOUND", pointOfSaleResponse), HttpStatus.OK);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDTO<PointOfSaleResponseDTO>> updatePointOfSale(@RequestBody @Valid PointOfSaleUpdateDTO pointOfSaleUpdateDTO) {
        PointOfSaleResponseDTO updatedPointOfSale = pointOfSaleService.update(pointOfSaleUpdateDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Point Of Sale Updated", updatedPointOfSale));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePontOfSale(@PathVariable Long id) {
        pointOfSaleService.delete(id);
        String message = "Point of Sale Successfully Deleted";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<PointOfSaleResponseDTO> getPointOfSaleInternal(@PathVariable Long id) {
        PointOfSaleResponseDTO pointOfSale = pointOfSaleService.findById(id);
        return ResponseEntity.ok(pointOfSale);
    }
}