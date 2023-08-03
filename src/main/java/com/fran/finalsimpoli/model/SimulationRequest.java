package com.fran.finalsimpoli.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SimulationRequest {
    private double time;
    private int iteraciones;
    private double desdeHora;

    private double limpieza;

    private double llegadaFutbolE;
    private double llegadaHandBallMedia;
    private double llegadaHandBallDesvi;
    private double llegadaBasketBallMedia;
    private double llegadaBasketBallDesvi;

    private double finJuegoFutbolMedia;
    private double finJuegoFutbolDesvi;
    private double finJuegoHandBallMedia;
    private double finJuegoHandBallDesvi;
    private double finJuegoBasketBallMedia;
    private double finJuegoBasketBallDesvi;

}
