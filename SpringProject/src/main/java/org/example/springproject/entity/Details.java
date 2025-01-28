package org.example.springproject.entity;

public class Details {
    private String sensorName;
    private float value;
    private Integer port;

    public Details(){}
    public Details(String sensorName, float value, Integer port){
        this.sensorName = sensorName;
        this.value = value;
        this.port = port;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
