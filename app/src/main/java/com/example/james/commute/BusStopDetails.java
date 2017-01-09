package com.example.james.commute;

/**
 * Created by james on 12/2/2016.
 */

public class BusStopDetails {
    private String name;
    private int distance;
    private String ID;
    private LocCord location;


    public void setName(String newName){
        name=newName;
    }

    public void setDistance(int newDistance){
        distance = newDistance;
    }

    public void setID(String nid){
        ID= nid;
    }

    public void setLocation(LocCord newLoc){
        location=newLoc;
    }

    public String getName(){
        return name;
    }

    public int getDistance(){
        return distance;
    }

    public String getID(){
        return ID;
    }

    public LocCord getLocation(){
        return location;
    }
}
