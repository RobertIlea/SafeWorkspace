/**
 * AlertDTO.java
 * This class represents a Data Transfer Object (DTO) for alerts in the system.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.dto;

import com.google.cloud.Timestamp;

import java.util.Map;

/**
 * AlertDTO is a Data Transfer Object that encapsulates the details of an alert.
 * It is used to transfer alert data between different layers of the application.
 */
public class AlertDTO {

    /**
     * Unique identifier for the alert.
     */
    private String alertId;

    /**
     * Identifier for the room associated with the alert.
     */
    private String roomId;

    /**
     * Identifier for the sensor that triggered the alert.
     */
    private String sensorId;

    /**
     * Timestamp when the alert was created.
     */
    private Timestamp timestamp;

    /**
     * Type of the sensor that triggered the alert.
     */
    private String sensorType;

    /**
     * Data associated with the alert.
     */
    private Map<String,Float> data;

    /**
     * Message associated with the alert.
     */
    private String message;

    /**
     * Default constructor for AlertDTO.
     * Initializes a new instance of the AlertDTO class.
     */
    public AlertDTO() {
    }

    /**
     * Parameterized constructor for AlertDTO.
     * Initializes a new instance of the AlertDTO class with specified values.
     * @param alertId Unique identifier for the alert.
     * @param roomId Identifier for the room associated with the alert.
     * @param sensorId Identifier for the sensor that triggered the alert.
     * @param timestamp Timestamp when the alert was created.
     * @param sensorType Type of the sensor that triggered the alert.
     * @param data Data associated with the alert.
     * @param message Message associated with the alert.
     */
    public AlertDTO(String alertId, String roomId ,String sensorId, Timestamp timestamp, String sensorType, Map<String, Float> data, String message) {
        this.alertId = alertId;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.sensorId = sensorId;
        this.data = data;
        this.message = message;
    }

    /**
     * Gets the unique identifier for the alert.
     * @return The unique identifier for the alert.
     */
    public String getAlertId() {
        return alertId;
    }

    /**
     * Sets the unique identifier for the alert.
     * @param alertId The unique identifier for the alert.
     */
    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    /**
     * Gets the identifier for the room associated with the alert.
     * @return The identifier for the room associated with the alert.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the identifier for the room associated with the alert.
     * @param roomId The identifier for the room associated with the alert.
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Gets the identifier for the sensor that triggered the alert.
     * @return The identifier for the sensor that triggered the alert.
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * Sets the identifier for the sensor that triggered the alert.
     * @param sensorId The identifier for the sensor that triggered the alert.
     */
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Gets the timestamp when the alert was created.
     * @return The timestamp when the alert was created.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the alert was created.
     * @param timestamp The timestamp when the alert was created.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type of the sensor that triggered the alert.
     * @return The type of the sensor that triggered the alert.
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * Sets the type of the sensor that triggered the alert.
     * @param sensorType The type of the sensor that triggered the alert.
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Gets the data associated with the alert.
     * @return The data associated with the alert.
     */
    public Map<String, Float> getData() {
        return data;
    }

    /**
     * Sets the data associated with the alert.
     * @param data The data associated with the alert.
     */
    public void setData(Map<String, Float> data) {
        this.data = data;
    }

    /**
     * Gets the message associated with the alert.
     * @return The message associated with the alert.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the alert.
     * @param message The message associated with the alert.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of the AlertDTO object.
     * @return A string representation of the AlertDTO object.
     */
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
