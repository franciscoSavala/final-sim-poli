package com.fran.finalsimpoli.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResponseLine {
    private int n;
    private Evento evento;
    private double reloj;
    private Futbol futbolALlegar;
    private HandBall handBallALlegar;
    private BasketBall basketBallALlegar;


    private SimulationEvent jugando1;
    private SimulationEvent jugando2;

    private double finLimpieza;

    private Cancha cancha;
    private int colaHF;
    private int colaB;

    private double acumuladorEsperaFutbol;
    private double acumuladorEsperaHandBall;
    private double acumuladorEsperaBasketBall;

    private int acumuladorCantidadFutbolLlegaron;
    private int acumuladorCantidadHandBallLlegaron;
    private int acumuladorCantidadBasketBallLlegaron;

    private List<SimulationEvent> llegaron;

}
