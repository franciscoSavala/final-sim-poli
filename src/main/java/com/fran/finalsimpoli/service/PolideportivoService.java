package com.fran.finalsimpoli.service;

import com.fran.finalsimpoli.model.ResponseLine;
import com.fran.finalsimpoli.model.Simulation;
import com.fran.finalsimpoli.model.SimulationRequest;
import com.fran.finalsimpoli.model.SimulationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolideportivoService {
    @Autowired
    private Simulation simulation;
    public SimulationResponse simulate(SimulationRequest simulationRequest){
        simulation.reset();
        return simulation.startSimulation(simulationRequest);
    }

    public SimulationResponse getTable(Long page) {
        return SimulationResponse.builder().build();
    }

    public ResponseLine getLastLine() {
        return ResponseLine.builder().build();
    }
}
