package org.example.springproject.entity;

import org.example.springproject.util.SensorType;

public class Sensor {
    private SensorType sensorType;
    private Details details;

    public Sensor(){}
    public Sensor(SensorType sensorType, Details details) {
        this.sensorType = sensorType;
        this.details = details;
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
