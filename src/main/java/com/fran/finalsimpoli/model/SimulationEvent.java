package com.fran.finalsimpoli.model;

public interface SimulationEvent extends Comparable<SimulationEvent> {
    Double timeEvent();

    void execute(Simulation service, SimulationRequest simulationRequest);

    SimulationEvent copy();

    double acumular(double lastReloj, double reloj);

    void generarFinJuego(Simulation service, SimulationRequest simulationRequest);

    EstadoDisciplina estado();
}