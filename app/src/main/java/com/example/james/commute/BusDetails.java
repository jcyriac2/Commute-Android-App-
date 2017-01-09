package com.example.james.commute;


/**
 * Created by james on 12/19/2016.
 */

public class BusDetails {
    private String Number;
    private int ETA;
    private String Color;
    private String routeNumber;

    public void setNumber(String num){
        Number=num;
    }

    public void setETA(int eta){
        ETA=eta;
    }

    public void setColor(String color){
        Color = color;
    }

    public void setRouteNumber(String no){
        routeNumber=no;
    }

    public String getNumber(){
        return Number;
    }

    public int getETA(){
        return ETA;
    }

    public String getColor(){
        return  Color;
    }

    public String getRouteNumber(){
        return routeNumber;
    }
}
