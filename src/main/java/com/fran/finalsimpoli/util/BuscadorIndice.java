package com.fran.finalsimpoli.util;

import com.fran.finalsimpoli.model.EstadoDisciplina;
import com.fran.finalsimpoli.model.SimulationEvent;

import java.util.LinkedList;
import java.util.List;

public class BuscadorIndice {
    private List<SimulationEvent> subLista;
    private boolean encontrado;

    public BuscadorIndice(List<SimulationEvent> llegaron){
        this.encontrado = false;
        this.subLista = llegaron;
    }

    public List<SimulationEvent> buscarSubLista(){
        if(encontrado) return copiar();

        int ind = 0;
        for(SimulationEvent se : subLista){
            if(se.estado() != EstadoDisciplina.FIN_JUEGO){
                encontrado = true;
                subLista = subLista.subList(ind, subLista.size());
                return copiar();
            }
            ind++;
        }

        subLista.clear();
        return copiar();
    }

    private List<SimulationEvent> copiar() {
        List<SimulationEvent> copia = new LinkedList<>();
        for(SimulationEvent se : subLista){
            copia.add(se.copy());
        }
        return copia;
    }

    public void add(SimulationEvent se){
        subLista.add(se);
    }
}
