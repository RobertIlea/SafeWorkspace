package org.example.springproject.dto;

import com.google.cloud.Timestamp;

import java.util.Map;

public class AlertDTO {
    private String alertId;
    private String roomId;
    private String sensorId;
    private Timestamp timestamp;
    private String sensorType;
    private Map<String,Float> data;
    private String message;

    public AlertDTO() {
    }

    public AlertDTO(String alertId, String roomId ,String sensorId, Timestamp timestamp, String sensorType, Map<String, Float> data, String message) {
        this.alertId = alertId;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.sensorId = sensorId;
        this.data = data;
        this.message = message;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public Map<String, Float> getData() {
        return data;
    }

    public void setData(Map<String, Float> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "AlertDTO{" +
                "alertId='" + alertId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
