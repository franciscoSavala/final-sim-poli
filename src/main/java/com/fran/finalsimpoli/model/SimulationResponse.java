package com.fran.finalsimpoli.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Response;

import java.util.List;

@Getter
@Setter
@Builder
public class SimulationResponse {
    List<ResponseLine> data;
    double promedioEsperaFutbol;
    double promedioEsperaBasketBall;
    double promedioEsperaHandBall;

}
