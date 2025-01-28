package org.example.springproject.entity;

import java.util.List;

public class Room {
    private String userId;
    private List<Sensor> sensors;

    public Room(){}
    public Room(String userId, List<Sensor> sensors){
        this.userId = userId;
        this.sensors = sensors;
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
}
