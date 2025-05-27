package org.example.springproject.entity;

import com.google.cloud.Timestamp;

import java.util.Map;

public class CustomAlert extends Alert {
    private String userId;
    private String parameter;
    private String condition;
    private float threshold;

    public CustomAlert() {}

    public CustomAlert(String userId, String roomId, String sensorId, String sensorType, String parameter, String condition, float threshold, String message) {
        super(roomId, sensorId, null, sensorType, null, message);
        this.userId = userId;
        this.parameter = parameter;
        this.condition = condition;
        this.threshold = threshold;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "CustomAlert{" +
                "userId='" + userId + '\'' +
                ", parameter='" + parameter + '\'' +
                ", condition='" + condition + '\'' +
                ", threshold=" + threshold +
                ", roomId='" + getRoomId() + '\'' +
                ", sensorId='" + getSensorId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", sensorType='" + getSensorType() + '\'' +
                ", data=" + getData() +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
