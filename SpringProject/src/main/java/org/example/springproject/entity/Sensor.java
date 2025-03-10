package org.example.springproject.entity;


import java.util.List;

public class Sensor {
    private String sensorType;
    private Integer port;
    private List<Details> details;

    public Sensor(){}


    public Sensor(String sensorType, Integer port, List<Details> details) {
        this.sensorType = sensorType;
        this.port = port;
        this.details = details;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }
}
