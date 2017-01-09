package com.example.james.commute;

/**
 * Created by james on 11/24/2016.
 */

public class LocCord {
    private double lat;
    private double lon;

    public LocCord(double lon, double lat){
        this.lat=lat;
        this.lon=lon;
    }

    public void set(double lon,double lat){
        this.lat=lat;
        this.lon=lon;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }
}
