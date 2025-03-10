package org.example.springproject.dto;

import org.example.springproject.entity.Sensor;

import java.util.List;

public class RoomDTO {
    private String id;
    private List<Sensor> sensors;
    private String name;
    public RoomDTO() {
    }

    public RoomDTO(String id, List<Sensor> sensors, String name) {
        this.id = id;
        this.sensors = sensors;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
