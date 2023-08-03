package com.fran.finalsimpoli.model;

import lombok.*;

import java.util.Queue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Limpieza implements SimulationEvent{
    private double finLimpieza;


    @Override
    public Double timeEvent() {
        return finLimpieza;
    }

    @Override
    public void execute(Simulation service, SimulationRequest simulationRequest) {
        service.setEvento(Evento.FIN_LIMPIEZA);

        Cancha cancha = service.getCancha();
        service.setLimpieza(null);
        Queue<SimulationEvent> colaBasket = service.getCancha().getColaBasket();
        Queue<SimulationEvent> colaFutbolHandball = service.getCancha().getColaFutbolHandBall();
        if(!colaFutbolHandball.isEmpty()){
            SimulationEvent se = colaFutbolHandball.poll();
            se.generarFinJuego(service, simulationRequest);
            cancha.setJugando1(se);
            cancha.setEstado(EstadoCancha.OCUPADA_UN_GRUPO);
            return;
        }
        if(!colaBasket.isEmpty()){
            SimulationEvent se1 = colaBasket.poll();
            SimulationEvent se2 = colaBasket.poll();

            if(se1 != null) se1.generarFinJuego(service,simulationRequest);
            if(se2 != null) se2.generarFinJuego(service,simulationRequest);

            cancha.setJugando1(se1);
            cancha.setJugando2(se2);
            cancha.setEstado((cancha.getJugando2() != null) ? EstadoCancha.OCUPADA_DOS_GRUPOS : EstadoCancha.OCUPADA_UN_GRUPO);
            return;
        }
        cancha.setEstado(EstadoCancha.LIBRE);
    }

    @Override
    public SimulationEvent copy() {
        return new Limpieza(finLimpieza);
    }

    @Override
    public double acumular(double lastReloj) {
        return 0;
    }

    @Override
    public void generarFinJuego(Simulation service, SimulationRequest simulationRequest) {

    }

    @Override
    public EstadoDisciplina estado() {
        return null;
    }

    @Override
    public int compareTo(SimulationEvent o) {
        return Double.compare(timeEvent(), o.timeEvent());
    }
}
