package com.microservice.pointsalecost.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CostID implements Serializable {
    private Long idA;
    private Long idB;
}
