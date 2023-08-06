package com.fran.finalsimpoli.controller;

import com.fran.finalsimpoli.model.SimulationRequest;
import com.fran.finalsimpoli.model.SimulationResponse;
import com.fran.finalsimpoli.service.PolideportivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class PolideportivoController {

    @Autowired
    private PolideportivoService service;

    @PostMapping
    public ResponseEntity<SimulationResponse> simulation(@RequestBody SimulationRequest simulationRequest){
        return ResponseEntity.ok(service.simulate(simulationRequest));
    }

    @GetMapping
    public ResponseEntity<SimulationResponse> lastLine(){
        return ResponseEntity.ok(service.lastLine());

    }
}
