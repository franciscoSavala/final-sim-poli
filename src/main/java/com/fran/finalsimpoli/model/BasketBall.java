package com.fran.finalsimpoli.model;


import com.fran.finalsimpoli.util.Estadisticos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketBall implements SimulationEvent{
    private final static String tipo = "BASKET_BALL";

    private double rnd_llegada1;
    private double rnd_llegada2;
    private double llegada;
    private double rnd_fin_juego1;
    private double rnd_fin_juego2;
    private double fin_juego;
    private boolean llego;
    private EstadoDisciplina estado;

    public BasketBall(double rnd_llegada1, double rnd_llegada2) {
        this.rnd_llegada1 = rnd_llegada1;
        this.rnd_llegada2 = rnd_llegada2;
        this.llego = false;
        this.estado = EstadoDisciplina.NO_LLEGO;
    }

    @Override
    public Double timeEvent() {
        double comparator = llego ? fin_juego : llegada;
        return comparator;
    }

    @Override
    public void execute(Simulation service, SimulationRequest simulationRequest) {
        if(!llego){
            service.setTotalLlegadaBasketBall(service.getTotalLlegadaBasketBall() + 1);
            service.getLlegaron().add(this);


            Cancha cancha = service.getCancha();
            service.setEvento(Evento.LLEGADA_BASKET);

            if(service.getLlegadaBasketBallPrimero() == null){
                BasketBall basketBall = new BasketBall(service.getRandom().nextDouble(), service.getRandom().nextDouble());
                basketBall.setLlegada(service.getReloj() + Estadisticos.normalBoxMuller(
                        basketBall.getRnd_llegada1(),
                        basketBall.getRnd_llegada2(),
                        simulationRequest.getLlegadaBasketBallMedia(),
                        simulationRequest.getLlegadaBasketBallDesvi(),
                        true
                ));
                service.setLlegadaBasketBallPrimero(basketBall);
                service.setBasketBallPorllegar(basketBall);

            }else{
                BasketBall basketBall = new BasketBall(service.getLlegadaBasketBallPrimero().getRnd_llegada1(), service.getLlegadaBasketBallPrimero().getRnd_llegada2());
                basketBall.setLlegada(service.getReloj() + Estadisticos.normalBoxMuller(
                        basketBall.getRnd_llegada1(),
                        basketBall.getRnd_llegada2(),
                        simulationRequest.getLlegadaBasketBallMedia(),
                        simulationRequest.getLlegadaBasketBallDesvi(),
                        false
                ));
                service.setLlegadaBasketBallPrimero(null);
                service.setBasketBallPorllegar(basketBall);

            }


            if(cancha.getEstado() == EstadoCancha.LIBRE){

                generarFinJuego(service, simulationRequest);

                cancha.setEstado(EstadoCancha.OCUPADA_UN_GRUPO);
                cancha.setJugando1(this);
            }else{
                estado = EstadoDisciplina.ESPERANDO;
                cancha.getColaBasket().add(this);
            }
            llego = true;
        }else{
            service.setEvento(Evento.FIN_JUEGO);

            estado = EstadoDisciplina.FIN_JUEGO;
            fin_juego = Double.MAX_VALUE;
        }
    }

    public void generarFinJuego(Simulation service, SimulationRequest simulationRequest){
        boolean primero = false;
        if(service.getFinJuegoBasketBallPrimero() == null){
            rnd_fin_juego1 = service.getRandom().nextDouble();
            rnd_fin_juego2 = service.getRandom().nextDouble();
            service.setFinJuegoBasketBallPrimero(this);
            primero = true;
        }else{
            rnd_fin_juego1 = service.getFinJuegoBasketBallPrimero().getRnd_fin_juego1();
            rnd_fin_juego2 = service.getFinJuegoBasketBallPrimero().getRnd_fin_juego2();
            service.setFinJuegoBasketBallPrimero(null);
        }
        fin_juego = service.getReloj() + Estadisticos.normalBoxMuller(rnd_fin_juego1,
                rnd_fin_juego2, simulationRequest.getFinJuegoBasketBallMedia(),
                simulationRequest.getFinJuegoBasketBallDesvi(),
                primero);
        estado = EstadoDisciplina.JUGANDO;
    }

    @Override
    public EstadoDisciplina estado() {
        return estado;
    }

    @Override
    public SimulationEvent copy() {
        return BasketBall.builder()
                .estado(estado)
                .rnd_llegada1(rnd_llegada1)
                .rnd_llegada2(rnd_llegada2)
                .rnd_fin_juego1(rnd_fin_juego1)
                .rnd_fin_juego2(rnd_fin_juego2)
                .fin_juego(fin_juego)
                .llegada(llegada)
                .llego(llego)
                .build();
    }

    @Override
    public int compareTo(SimulationEvent o) {
        return Double.compare(timeEvent(),o.timeEvent());
    }

    @Override
    public double acumular(double lastReloj) {
        return lastReloj - llegada;
    }
}

