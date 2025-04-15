package com.microservice.pointsalecost.repositories;

import com.microservice.pointsalecost.models.PointOfSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointOfSaleRepository extends JpaRepository<PointOfSale, Long> {
}