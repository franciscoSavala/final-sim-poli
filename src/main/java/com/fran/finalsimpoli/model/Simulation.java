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

        this.acumuladorEsperaFutbol = 0;
        this.acumuladorEsperaBasketBall = 0;
        this.acumuladorEsperaHandBall = 0;


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


        double maxTime = simulationRequest.getTime();
        int iter = simulationRequest.getIteraciones();
        double aPartirDeHora = simulationRequest.getDesdeHora();
        int iteracionActual = 0;

        double lastReloj = reloj;
        while(reloj < maxTime){
            acumuladorEsperaFutbol += cancha.acumularDisciplinaDesde(Futbol.class, lastReloj, reloj);
            acumuladorEsperaHandBall += cancha.acumularDisciplinaDesde(HandBall.class, lastReloj, reloj);
            acumuladorEsperaBasketBall += cancha.acumularDisciplinaDesde(BasketBall.class, lastReloj, reloj);

            if(reloj >= aPartirDeHora && iteracionActual < iter){
                ResponseLine responseLine = mapperLine((LinkedList<ResponseLine>) data);
                data.add(responseLine);
                iteracionActual++;
            }

            SimulationEvent actual = encontrarProximoEvento();
            lastReloj = reloj;
            reloj = actual.timeEvent();

            if(reloj > maxTime){
                reloj = lastReloj;
                break;
            }

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

    private ResponseLine mapperLine(LinkedList<ResponseLine> data) {
        List<SimulationEvent> sefh = llegaronFutbolHandball.buscarSubLista();
        List<SimulationEvent> seb = llegaronBasket.buscarSubLista();
        Futbol fal = (Futbol) futbolPorllegar.copy();
        HandBall hal = (HandBall) handBallPorllegar.copy();
        BasketBall bal = (BasketBall) basketBallPorllegar.copy();

        SimulationEvent sj1 = (cancha.getJugando1() != null) ? cancha.getJugando1().copy() : null;
        SimulationEvent sj2 = (cancha.getJugando2() != null) ? cancha.getJugando2().copy() : null;

        if(!data.isEmpty()){
            if(data.getLast().getFutbolALlegar().getLlegada() == futbolPorllegar.getLlegada()) fal = Futbol.builder()
                    .rnd_llegada(Double.MAX_VALUE)
                    .llegada(futbolPorllegar.getLlegada()).build();

            if(data.getLast().getHandBallALlegar().getLlegada() == handBallPorllegar.getLlegada()) hal = HandBall.builder()
                    .rnd_llegada1(Double.MAX_VALUE)
                    .rnd_llegada2(Double.MAX_VALUE)
                    .llegada(handBallPorllegar.getLlegada()).build();

            if(data.getLast().getBasketBallALlegar().getLlegada() == basketBallPorllegar.getLlegada()) bal = BasketBall.builder()
                    .rnd_llegada1(Double.MAX_VALUE)
                    .rnd_llegada2(Double.MAX_VALUE)
                    .llegada(basketBallPorllegar.getLlegada()).build();


            if(data.getLast().getJugando1() != null && sj1 != null && data.getLast().getJugando1().getFinJuego() == sj1.getFinJuego()) sj1 = Futbol.builder()
                    .rnd_fin_juego1(Double.MAX_VALUE)
                    .rnd_fin_juego2(Double.MAX_VALUE)
                    .fin_juego(sj1.getFinJuego()).build();
            if(data.getLast().getJugando2() != null && sj2 != null && data.getLast().getJugando2().getFinJuego() == sj2.getFinJuego()) sj2 = Futbol.builder()
                    .rnd_fin_juego1(Double.MAX_VALUE)
                    .rnd_fin_juego2(Double.MAX_VALUE)
                    .fin_juego(sj2.getFinJuego()).build();
        }



        return ResponseLine.builder()
                .n(n)
                .evento(evento)
                .reloj(reloj)
                .futbolALlegar(fal)
                .handBallALlegar(hal)
                .basketBallALlegar(bal)
                .jugando1(sj1)
                .jugando2(sj2)
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

    public SimulationResponse lastLine() {
        LinkedList<ResponseLine> rl = new LinkedList<>();
        rl.add(ResponseLine.builder()
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
                .build());


        return SimulationResponse.builder()
                .data(rl)
                .promedioEsperaFutbol((totalLlegadaFutbol != 0) ? acumuladorEsperaFutbol / totalLlegadaFutbol : Double.MAX_VALUE)
                .promedioEsperaHandBall((totalLlegadaHandBall != 0) ? acumuladorEsperaHandBall / totalLlegadaHandBall : Double.MAX_VALUE)
                .promedioEsperaBasketBall((totalLlegadaBasketBall != 0) ? acumuladorEsperaBasketBall / totalLlegadaBasketBall : Double.MAX_VALUE)
                .build();
    }
}

