package com.fran.finalsimpoli.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Cancha implements SimulationEvent{
    private EstadoCancha estado;

    @JsonIgnore
    private Queue<SimulationEvent> colaBasket;
    @JsonIgnore
    private Queue<SimulationEvent> colaFutbolHandBall;

    @JsonIgnore
    private SimulationEvent jugando1;
    @JsonIgnore
    private SimulationEvent jugando2;

    public Cancha(){
        this.estado = EstadoCancha.LIBRE;
        this.colaBasket = new LinkedList<>();
        this.colaFutbolHandBall = new LinkedList<>();
        this.jugando1 = null;
        this.jugando2 = null;
    }

    @Override
    public Double timeEvent() {
        double first = jugando1 != null ? jugando1.timeEvent() : Double.MAX_VALUE;
        double second = jugando2 != null ? jugando2.timeEvent() : Double.MAX_VALUE;
        return Double.min(first, second);
    }

    @Override
    public void execute(Simulation service, SimulationRequest simulationRequest) {
        service.setEvento(Evento.FIN_JUEGO);



        if(jugando1 != null && service.getReloj() == jugando1.timeEvent()){
            jugando1.execute(service,simulationRequest);
            jugando1 = null;
        }else{
            jugando2.execute(service,simulationRequest);
            jugando2 = null;
        }

        if(jugando1 == null && jugando2 == null){
            service.setLimpieza(new Limpieza(service.getReloj() + simulationRequest.getLimpieza()));
            estado = EstadoCancha.RECIBIENDO_LIMPIEZA;
            return;
        }

    }

    @Override
    public SimulationEvent copy(){
        Queue<SimulationEvent> cb = new LinkedList<>();
        for(SimulationEvent b : colaBasket) cb.add(b.copy());

        Queue<SimulationEvent> chf = new LinkedList<>();
        for(SimulationEvent hf : colaFutbolHandBall) chf.add(hf.copy());

        return Cancha.builder()
                .colaBasket(cb)
                .colaFutbolHandBall(chf)
                .estado(estado)
                .build();
    }

    @Override
    public double acumular(double lastReloj, double reloj) {
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
        return Double.compare(this.timeEvent(), o.timeEvent());
    }

    public SimulationEvent encontrarProximoEvento(List<SimulationEvent> se) {
        if(estado != EstadoCancha.LIBRE){
            se.add(this);
        }
        SimulationEvent menor = Collections.min(se);

        // DEBERIA DEVOLVER LA CANCHA PERO DEVUELVE LA DISCIPLINA, POR ESO NO SE HACE EL FIN JUEGO!!!
        return menor;

    }

    public double acumularDisciplinaDesde(Class disciplinaClass, double lastReloj, double reloj) {
        double acumulador = 0;
        for(SimulationEvent se : colaFutbolHandBall) acumulador += (disciplinaClass.isInstance(se)) ? se.acumular(lastReloj, reloj) : 0;
        for(SimulationEvent se : colaBasket) acumulador += (disciplinaClass.isInstance(se)) ? se.acumular(lastReloj, reloj) : 0;

        return acumulador;
    }
}
