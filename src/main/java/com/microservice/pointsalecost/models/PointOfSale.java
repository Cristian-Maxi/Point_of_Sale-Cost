package com.microservice.pointsalecost.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Point_of_Sale")
public class PointOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean active;

    public PointOfSale(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}