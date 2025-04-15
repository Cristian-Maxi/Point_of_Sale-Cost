package com.microservice.pointsalecost.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Cost")
public class Cost implements Serializable {

    @EmbeddedId
    private CostID id;
    private Double amount;

    public Cost(Long idA, Long idB, Double amount) {
        this.id = new CostID(idA, idB);
        this.amount = amount;
    }

    @JsonIgnore
    public Long getIdA() {
        return id.getIdA();
    }

    @JsonIgnore
    public Long getIdB() {
        return id.getIdB();
    }
}
