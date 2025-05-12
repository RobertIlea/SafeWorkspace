package org.example.springproject.dto;

import org.example.springproject.entity.Details;
import org.example.springproject.util.SensorType;

import java.util.List;
import java.util.Map;

public class SensorDTO {
    private String id;
    private String sensorType;
    private Integer port;
    private List<Details> details;

    public SensorDTO(){}

    public SensorDTO(String id, String sensorType,Integer port, List<Details> details) {
        this.id = id;
        this.sensorType = sensorType;
        this.port = port;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "SensorDTO{" +
                "id='" + id + '\'' +
                ", sensorType='" + sensorType + '\'' +
                ", port=" + port +
                ", details=" + details +
                '}';
    }
}
