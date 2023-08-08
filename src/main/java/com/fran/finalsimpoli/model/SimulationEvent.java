package com.fran.finalsimpoli.model;

import java.util.LinkedList;

public interface SimulationEvent extends Comparable<SimulationEvent> {
    Double timeEvent();

    void execute(Simulation service, SimulationRequest simulationRequest);

    SimulationEvent copy();

    double acumular(double lastReloj, double reloj);

    void generarFinJuego(Simulation service, SimulationRequest simulationRequest);

    EstadoDisciplina estado();

    double getFinJuego();
}