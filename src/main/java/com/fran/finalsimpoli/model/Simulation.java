package com.fran.finalsimpoli.model;


import com.fran.finalsimpoli.util.BuscadorIndice;
import com.fran.finalsimpoli.util.Estadisticos;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;

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

    private BuscadorIndice llegaronBasket;
    private BuscadorIndice llegaronFutbolHandball;

    private Random random;

    private Futbol finJuegoFutbolPrimero;
    private HandBall llegadaHandBallPrimero;
    private HandBall finJuegoHandBallPrimero;
    private BasketBall llegadaBasketBallPrimero;
    private BasketBall finJuegoBasketBallPrimero;

    private Limpieza limpieza;

    private int indice;

    private boolean primeraVuelta;


    public void reset() {
        this.n = 0;
        this.evento = Evento.INICIO;
        this.reloj = 0;

        this.cancha = new Cancha();

        this.random = new Random();

        this.llegaronBasket = new BuscadorIndice(new LinkedList<>());
        this.llegaronFutbolHandball = new BuscadorIndice(new LinkedList<>());

        this.finJuegoFutbolPrimero = null;
        this.llegadaHandBallPrimero = null;
        this.finJuegoHandBallPrimero = null;
        this.llegadaBasketBallPrimero = null;
        this.finJuegoBasketBallPrimero = null;

        this.limpieza = null;

        this.totalLlegadaFutbol = 0;
        this.totalLlegadaBasketBall = 0;
        this.totalLlegadaHandBall = 0;

        this.primeraVuelta = true;
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

        double lastReloj = reloj;
        while(reloj < maxTime){
            acumuladorEsperaFutbol += cancha.acumularDisciplinaDesde(Futbol.class, lastReloj, reloj);
            acumuladorEsperaHandBall += cancha.acumularDisciplinaDesde(HandBall.class, lastReloj, reloj);
            acumuladorEsperaBasketBall += cancha.acumularDisciplinaDesde(BasketBall.class, lastReloj, reloj);

            if(reloj >= aPartirDeHora && iteracionActual < iter){
                ResponseLine responseLine = mapperLine();
                data.add(responseLine);
                iteracionActual++;
            }

            SimulationEvent actual = encontrarProximoEvento();
            lastReloj = reloj;
            reloj = actual.timeEvent();

            actual.execute(this, simulationRequest);
            n++;

        }

        return SimulationResponse.builder().data(data).build();
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
        List<SimulationEvent> sefh = llegaronFutbolHandball.buscarSubLista();
        List<SimulationEvent> seb = llegaronBasket.buscarSubLista();

        return ResponseLine.builder()
                .n(n)
                .evento(evento)
                .reloj(reloj)
                .futbolALlegar((Futbol)futbolPorllegar.copy())
                .handBallALlegar((HandBall)handBallPorllegar.copy())
                .basketBallALlegar((BasketBall)basketBallPorllegar.copy())
                .jugando1((cancha.getJugando1() != null) ? cancha.getJugando1().copy() : null)
                .jugando2((cancha.getJugando2() != null) ? cancha.getJugando2().copy() : null)
                .cancha((Cancha)cancha.copy())
                .colaHF(cancha.getColaFutbolHandBall().size())
                .colaB(cancha.getColaBasket().size())
                .acumuladorEsperaFutbol(acumuladorEsperaFutbol)
                .acumuladorEsperaBasketBall(acumuladorEsperaBasketBall)
                .acumuladorEsperaHandBall(acumuladorEsperaHandBall)
                .acumuladorCantidadBasketBallLlegaron(totalLlegadaBasketBall)
                .acumuladorCantidadHandBallLlegaron(totalLlegadaHandBall)
                .acumuladorCantidadFutbolLlegaron(totalLlegadaFutbol)
                .finLimpieza((limpieza != null)? limpieza.getFinLimpieza() : Double.MAX_VALUE)
                .llegaronFutbolHandBall(sefh)
                .llegaronBasketBall(seb)
                .build();
    }
}

