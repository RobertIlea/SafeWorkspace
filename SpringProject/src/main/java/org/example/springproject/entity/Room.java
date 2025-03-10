package org.example.springproject.entity;

import java.util.List;

public class Room {
    private String userId;
    private List<Sensor> sensors;
    private String name;

    public Room(){}

    public Room(String userId, List<Sensor> sensors, String name){
        this.userId = userId;
        this.sensors = sensors;
        this.name = name;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
