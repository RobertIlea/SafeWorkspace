package org.example.springproject.dto;

import org.example.springproject.entity.Sensor;
import org.example.springproject.entity.User;

import java.util.List;

public class RoomDTO {
    private String id;
    private List<Sensor> sensors;

    public RoomDTO() {
    }

    public RoomDTO(String id, List<Sensor> sensors) {
        this.id = id;

        this.sensors = sensors;
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
}
