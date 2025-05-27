package org.example.springproject.dto;

public class CustomAlertDTO {
    private String id;
    private String userId;
    private String roomId;
    private String sensorId;
    private String sensorType;
    private String parameter;
    private String condition;
    private float threshold;
    private String message;

    public CustomAlertDTO() {
    }

    public CustomAlertDTO(String id,String userId, String roomId, String sensorId, String sensorType, String parameter, String condition, String message, float threshold) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.parameter = parameter;
        this.condition = condition;
        this.message = message;
        this.threshold = threshold;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
