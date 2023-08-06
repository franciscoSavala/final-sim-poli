package com.fran.finalsimpoli.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoCancha {
    LIBRE("LIBRE"),
    OCUPADA_UN_GRUPO("OCUPADO"),
    OCUPADA_DOS_GRUPOS("OCUPADO"),
    RECIBIENDO_LIMPIEZA("OCUPADO");

    // Member to hold the name
    @JsonValue
    private String string;

    // constructor to set the string
    EstadoCancha(String name){string = name;}

    // the toString just returns the given name
    @Override
    public String toString() {
        return string;
    }


}
