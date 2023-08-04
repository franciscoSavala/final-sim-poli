package com.fran.finalsimpoli.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fran.finalsimpoli.util.Estadisticos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Futbol implements SimulationEvent{

    private String tipo;

    private double rnd_llegada;
    private double llegada;
    private double rnd_fin_juego1;
    private double rnd_fin_juego2;
    private double fin_juego;

    @JsonIgnore
    private boolean llego;

    private EstadoDisciplina estado;

    public Futbol(double rnd_llegada) {
        this.rnd_llegada = rnd_llegada;
        this.llego = false;
        this.estado = EstadoDisciplina.NO_LLEGO;
        this.tipo = "FUTBOL";
    }

    @Override
    public Double timeEvent() {
        double comparator = llego ? fin_juego : llegada;
        return comparator;
    }


    @Override
    public void execute(Simulation service, SimulationRequest simulationRequest) {
        if(!llego){
            service.setTotalLlegadaFutbol(service.getTotalLlegadaFutbol() + 1);
            service.getLlegaronFutbolHandball().add(this);

            Cancha cancha = service.getCancha();
            service.setEvento(Evento.LLEGADA_FUTBOL);


            Futbol porLlegar = new Futbol(service.getRandom().nextDouble());
            porLlegar.setLlegada(service.getReloj() + Estadisticos.exponential(porLlegar.getRnd_llegada(), simulationRequest.getLlegadaFutbolE()));
            service.setFutbolPorllegar(porLlegar);
            if(cancha.getEstado() == EstadoCancha.LIBRE){

                generarFinJuego(service, simulationRequest);

                cancha.setEstado(EstadoCancha.OCUPADA_UN_GRUPO);
                cancha.setJugando1(this);
            }else{
                estado = EstadoDisciplina.ESPERANDO;
                cancha.getColaFutbolHandBall().add(this);
            }
            llego = true;

        }else{
            estado = EstadoDisciplina.FIN_JUEGO;
            fin_juego = Double.MAX_VALUE;
        }
    }

    @Override
    public SimulationEvent copy() {
        return Futbol.builder()
                .estado(estado)
                .rnd_llegada(rnd_llegada)
                .rnd_fin_juego1(rnd_fin_juego1)
                .rnd_fin_juego2(rnd_fin_juego2)
                .fin_juego(fin_juego)
                .llegada(llegada)
                .llego(llego)
                .tipo(tipo)
                .build();
    }

    @Override
    public double acumular(double lastReloj) {
        return lastReloj - llegada;
    }

    @Override
    public void generarFinJuego(Simulation service, SimulationRequest simulationRequest) {
        boolean primero = false;
        if(service.getFinJuegoFutbolPrimero() == null){
            rnd_fin_juego1 = service.getRandom().nextDouble();
            rnd_fin_juego2 = service.getRandom().nextDouble();
            service.setFinJuegoFutbolPrimero(this);
            primero = true;
        }else{
            rnd_fin_juego1 = service.getFinJuegoFutbolPrimero().getRnd_fin_juego1();
            rnd_fin_juego2 = service.getFinJuegoFutbolPrimero().getRnd_fin_juego2();
            service.setFinJuegoFutbolPrimero(null);
        }
        fin_juego = service.getReloj() + Estadisticos.normalBoxMuller(rnd_fin_juego1,
                rnd_fin_juego2, simulationRequest.getFinJuegoFutbolMedia(),
                simulationRequest.getFinJuegoFutbolDesvi(),
                primero);
        estado = EstadoDisciplina.JUGANDO;
    }

    @Override
    public EstadoDisciplina estado() {
        return estado;
    }

    @Override
    public int compareTo(SimulationEvent o) {
        return Double.compare(timeEvent(),o.timeEvent());
    }
}
