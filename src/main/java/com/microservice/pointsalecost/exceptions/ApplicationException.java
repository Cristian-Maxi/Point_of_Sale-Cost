package com.microservice.pointsalecost.exceptions;

import lombok.*;

@Setter @Getter
public class ApplicationException extends RuntimeException{

    private String campo;

    public ApplicationException(String mensaje) {
        super(mensaje);
    }

    public ApplicationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
}
