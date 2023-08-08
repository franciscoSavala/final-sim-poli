package com.fran.finalsimpoli.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fran.finalsimpoli.util.Estadisticos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HandBall implements SimulationEvent {

    private String tipo;

    private double rnd_llegada1;
    private double rnd_llegada2;
    private double llegada;
    private double rnd_fin_juego1;
    private double rnd_fin_juego2;
    private double fin_juego;
    @JsonIgnore
    private boolean llego;

    private EstadoDisciplina estado;


    public HandBall(double rnd_llegada1, double rnd_llegada2) {
        this.rnd_llegada1 = rnd_llegada1;
        this.rnd_llegada2 = rnd_llegada2;
        this.llego = false;
        this.estado = EstadoDisciplina.NO_LLEGO;
        this.tipo = "HAND_BALL";
    }

    @Override
    public Double timeEvent() {
        double comparator = llego ? fin_juego : llegada;
        return comparator;
    }

    @Override
    public void execute(Simulation service, SimulationRequest simulationRequest) {
        if(!llego){
            service.setTotalLlegadaHandBall(service.getTotalLlegadaHandBall() + 1);
            service.getLlegaronFutbolHandball().add(this);


            Cancha cancha = service.getCancha();
            service.setEvento(Evento.LLEGADA_HANDBALL);


            if(service.getLlegadaHandBallPrimero() == null){
                HandBall handBall = new HandBall(service.getRandom().nextDouble(), service.getRandom().nextDouble());
                handBall.setLlegada(service.getReloj() + Estadisticos.normalBoxMuller(
                        handBall.getRnd_llegada1(),
                        handBall.getRnd_llegada2(),
                        simulationRequest.getLlegadaHandBallMedia(),
                        simulationRequest.getLlegadaHandBallDesvi(),
                        true
                ));
                service.setLlegadaHandBallPrimero(handBall);
                service.setHandBallPorllegar(handBall);
            }else{

                HandBall handBall = new HandBall(service.getLlegadaHandBallPrimero().getRnd_llegada1(), service.getLlegadaHandBallPrimero().getRnd_llegada2());
                handBall.setLlegada(service.getReloj() + Estadisticos.normalBoxMuller(
                        handBall.getRnd_llegada1(),
                        handBall.getRnd_llegada2(),
                        simulationRequest.getLlegadaHandBallMedia(),
                        simulationRequest.getLlegadaHandBallDesvi(),
                        false
                ));
                service.setLlegadaHandBallPrimero(null);
                service.setHandBallPorllegar(handBall);
            }


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
        return HandBall.builder()
                .estado(estado)
                .rnd_llegada1(rnd_llegada1)
                .rnd_llegada2(rnd_llegada2)
                .rnd_fin_juego1(rnd_fin_juego1)
                .rnd_fin_juego2(rnd_fin_juego2)
                .fin_juego(fin_juego)
                .llegada(llegada)
                .llego(llego)
                .tipo(tipo)
                .build();
    }
    @Override
    public int compareTo(SimulationEvent o) {
        return Double.compare(timeEvent(),o.timeEvent());
    }

    @Override
    public double acumular(double lastReloj, double reloj) {
        if(reloj == llegada) return 0;
        return reloj - lastReloj;
    }
    @Override
    public void generarFinJuego(Simulation service, SimulationRequest simulationRequest) {
        boolean primero = false;
        if(service.getFinJuegoHandBallPrimero() == null){
            rnd_fin_juego1 = service.getRandom().nextDouble();
            rnd_fin_juego2 = service.getRandom().nextDouble();
            service.setFinJuegoHandBallPrimero(this);
            primero = true;
        }else{
            rnd_fin_juego1 = service.getFinJuegoHandBallPrimero().getRnd_fin_juego1();
            rnd_fin_juego2 = service.getFinJuegoHandBallPrimero().getRnd_fin_juego2();
            service.setFinJuegoHandBallPrimero(null);
        }
        fin_juego = service.getReloj() + Estadisticos.normalBoxMuller(rnd_fin_juego1,
                rnd_fin_juego2, simulationRequest.getFinJuegoHandBallMedia(),
                simulationRequest.getFinJuegoHandBallDesvi(),
                primero);
        estado = EstadoDisciplina.JUGANDO;
    }

    @Override
    public EstadoDisciplina estado() {
        return estado;
    }

    @Override
    public double getFinJuego() {
        return fin_juego;
    }
}
