package org.example.springproject.dto;

import org.example.springproject.entity.Details;
import org.example.springproject.util.SensorType;

public class SensorDTO {
    private String id;
    private SensorType sensorType;
    private Details details;

    public SensorDTO(){}

    public SensorDTO(String id, SensorType sensorType, Details details) {
        this.id = id;
        this.sensorType = sensorType;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
}
