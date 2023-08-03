package com.fran.finalsimpoli.model;


import com.fran.finalsimpoli.util.Estadisticos;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@Component
@Getter
@Setter
public class Simulation {
    private int n;
    private Evento evento;
    private double reloj;

    private Futbol futbolPorllegar;
    private HandBall handBallPorllegar;
    private BasketBall basketBallPorllegar;

    private Cancha cancha;

    private double acumuladorEsperaFutbol;
    private double acumuladorEsperaHandBall;
    private double acumuladorEsperaBasketBall;

    private int totalLlegadaFutbol;
    private int totalLlegadaHandBall;
    private int totalLlegadaBasketBall;

    private List<SimulationEvent> llegaron;

    private Random random;

    private Futbol finJuegoFutbolPrimero;
    private HandBall llegadaHandBallPrimero;
    private HandBall finJuegoHandBallPrimero;
    private BasketBall llegadaBasketBallPrimero;
    private BasketBall finJuegoBasketBallPrimero;

    private Limpieza limpieza;

    private int indice;

    public void reset() {
        this.n = 0;
        this.evento = Evento.INICIO;
        this.reloj = 0;

        this.cancha = new Cancha();

        this.random = new Random();

        this.llegaron = new LinkedList<>();

        this.finJuegoFutbolPrimero = null;
        this.llegadaHandBallPrimero = null;
        this.finJuegoHandBallPrimero = null;
        this.llegadaBasketBallPrimero = null;
        this.finJuegoBasketBallPrimero = null;

        this.limpieza = null;

        this.totalLlegadaFutbol = 0;
        this.totalLlegadaBasketBall = 0;
        this.totalLlegadaHandBall = 0;
    }

    public SimulationResponse startSimulation(SimulationRequest simulationRequest) {
        List<ResponseLine> data = new LinkedList<>();

        this.futbolPorllegar = new Futbol(random.nextDouble());
        this.handBallPorllegar = new HandBall(random.nextDouble(), random.nextDouble());
        this.basketBallPorllegar = new BasketBall(random.nextDouble(), random.nextDouble());
        this.futbolPorllegar.setLlegada(Estadisticos.exponential(this.futbolPorllegar.getRnd_llegada(), simulationRequest.getLlegadaFutbolE()));
        this.handBallPorllegar.setLlegada(Estadisticos.normalBoxMuller(
                this.handBallPorllegar.getRnd_llegada1(),
                this.handBallPorllegar.getRnd_llegada2(),
                simulationRequest.getLlegadaHandBallMedia(),
                simulationRequest.getLlegadaHandBallDesvi(),
                true
        ));
        this.basketBallPorllegar.setLlegada(Estadisticos.normalBoxMuller(
                this.basketBallPorllegar.getRnd_llegada1(),
                this.basketBallPorllegar.getRnd_llegada2(),
                simulationRequest.getLlegadaBasketBallMedia(),
                simulationRequest.getLlegadaBasketBallDesvi(),
                true
        ));

        llegadaHandBallPrimero = handBallPorllegar;
        llegadaBasketBallPrimero = basketBallPorllegar;


        Double maxTime = simulationRequest.getTime();
        Integer iter = simulationRequest.getIteraciones();
        Double aPartirDeHora = simulationRequest.getDesdeHora();
        Integer iteracionActual = 0;
        boolean primeraVuelta = true;

        while(reloj < maxTime){
            double lastReloj = reloj;
            acumuladorEsperaFutbol = cancha.acumularDisciplinaDesde(Futbol.class, lastReloj);
            acumuladorEsperaHandBall = cancha.acumularDisciplinaDesde(HandBall.class, lastReloj);
            acumuladorEsperaBasketBall = cancha.acumularDisciplinaDesde(BasketBall.class, lastReloj);

            if(reloj >= aPartirDeHora && iteracionActual < iter){
                if(primeraVuelta){
                    buscarIndice();
                    primeraVuelta = false;
                }
                ResponseLine responseLine = mapperLine();
                data.add(responseLine);
                iteracionActual++;
            }

            SimulationEvent actual = encontrarProximoEvento();
            reloj = actual.timeEvent();

            actual.execute(this, simulationRequest);
            n++;

        }

        return SimulationResponse.builder().data(data).build();
    }

    private void buscarIndice() {
        int ind = 0;
        for(SimulationEvent se : llegaron){
            if(se.estado() != EstadoDisciplina.FIN_JUEGO){
                indice = ind;
                return;
            }
            ind++;
        }
    }

    private SimulationEvent encontrarProximoEvento() {
        List<SimulationEvent> se = new LinkedList<>();
        se.add(futbolPorllegar);
        se.add(basketBallPorllegar);
        se.add(handBallPorllegar);
        if(limpieza != null) se.add(limpieza);
        return cancha.encontrarProximoEvento(se);
    }

    private ResponseLine mapperLine() {
        List<SimulationEvent> se = new LinkedList<>();

        for(int i = indice; i<llegaron.size(); i++) se.add(llegaron.get(i).copy());


        return ResponseLine.builder()
                .n(n)
                .evento(evento)
                .reloj(reloj)
                .futbolALlegar((Futbol)futbolPorllegar.copy())
                .handBallALlegar((HandBall)handBallPorllegar.copy())
                .basketBallALlegar((BasketBall)basketBallPorllegar.copy())
                .jugando1(cancha.getJugando1())
                .jugando2(cancha.getJugando2())
                .cancha((Cancha)cancha.copy())
                .colaHF(cancha.getColaFutbolHandBall().size())
                .colaB(cancha.getColaBasket().size())
                .acumuladorEsperaFutbol(acumuladorEsperaFutbol)
                .acumuladorEsperaBasketBall(acumuladorEsperaBasketBall)
                .acumuladorEsperaBasketBall(acumuladorEsperaBasketBall)
                .finLimpieza((limpieza != null)? limpieza.getFinLimpieza() : Double.MAX_VALUE)
                .llegaron(se)
                .build();
    }
}

