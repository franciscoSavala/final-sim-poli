package com.fran.finalsimpoli.util;

public class Estadisticos {

    public static double normalBoxMuller(double rnd1, double rnd2, double mean, double dev, boolean first){
        double sq = Math.sqrt(-2 * Math.log(rnd1));
        double z1 = sq * Math.cos(2 * Math.PI * rnd2);
        double z2 = sq * Math.sin(2 * Math.PI * rnd2);

        double z = first ? z1 : z2;
        return z * dev + mean;
    }

    public static double exponential(double rnd, double mean){
        return - mean * Math.log(1-rnd);
    }
}
