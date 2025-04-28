package org.example.springproject.dto;

import org.example.springproject.entity.Sensor;

import java.util.List;

public class RoomDTO {
    private String id;
    private List<SensorDTO> sensors;
    private String name;
    private String userId;
    public RoomDTO() {
    }

    public RoomDTO(String id, List<SensorDTO> sensors, String name, String userId) {
        this.id = id;
        this.sensors = sensors;
        this.name = name;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<SensorDTO> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorDTO> sensors) {
        this.sensors = sensors;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @Override
    public String toString(){
        return "room id: " + id + "Senzori: " + sensors + "nume camera: " + name + "user id: " + userId;
    }
}
