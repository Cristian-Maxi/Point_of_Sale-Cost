package com.microservice.pointsalecost.repositories;

import com.microservice.pointsalecost.models.Cost;
import com.microservice.pointsalecost.models.CostID;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostRepository extends JpaRepository<Cost, CostID> {

    @Query("SELECT c FROM Cost c WHERE c.id.idA = :idA OR c.id.idB = :idB")
    List<Cost> findByIdAOrIdB(@Param("idA") Long idA, @Param("idB") Long idB);
}