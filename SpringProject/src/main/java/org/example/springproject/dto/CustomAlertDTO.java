/**
 * CustomAlertDTO.java
 * This class represents a Data Transfer Object (DTO) for custom alerts in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.dto;

/**
 * CustomAlertDTO is used to transfer data related to custom alerts between different layers of the application.
 */
public class CustomAlertDTO {

    /**
     * Unique identifier for the custom alert.
     */
    private String id;

    /**
     * Identifier for the user associated with the custom alert.
     */
    private String userId;

    /**
     * Identifier for the room associated with the custom alert.
     */
    private String roomId;

    /**
     * Identifier for the sensor associated with the custom alert.
     */
    private String sensorId;

    /**
     * Type of the sensor associated with the custom alert.
     */
    private String sensorType;

    /**
     * Parameter to be monitored for the custom alert.
     */
    private String parameter;

    /**
     * Condition that triggers the custom alert.
     */
    private String condition;

    /**
     * Threshold value for the custom alert.
     */
    private float threshold;

    /**
     * Message to be sent when the custom alert is triggered.
     */
    private String message;

    /**
     * Default constructor for CustomAlertDTO.
     */
    public CustomAlertDTO() {
    }

    /**
     * Parameterized constructor for CustomAlertDTO.
     * @param id Unique identifier for the custom alert.
     * @param userId Identifier for the user associated with the custom alert.
     * @param roomId Identifier for the room associated with the custom alert.
     * @param sensorId Identifier for the sensor associated with the custom alert.
     * @param sensorType Type of the sensor associated with the custom alert.
     * @param parameter Parameter to be monitored for the custom alert.
     * @param condition Condition that triggers the custom alert.
     * @param message Message to be sent when the custom alert is triggered.
     * @param threshold Threshold value for the custom alert.
     */
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

    /**
     * Get the unique identifier for the custom alert.
     * @return the unique identifier for the custom alert.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the custom alert.
     * @param id Unique identifier for the custom alert.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the identifier for the user associated with the custom alert.
     * @return the identifier for the user associated with the custom alert.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the identifier for the user associated with the custom alert.
     * @param userId Identifier for the user associated with the custom alert.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the identifier for the room associated with the custom alert.
     * @return the identifier for the room associated with the custom alert.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the identifier for the room associated with the custom alert.
     * @param roomId Identifier for the room associated with the custom alert.
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Get the type of the sensor associated with the custom alert.
     * @return the type of the sensor associated with the custom alert.
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * Sets the type of the sensor associated with the custom alert.
     * @param sensorType Type of the sensor associated with the custom alert.
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Get the identifier for the sensor associated with the custom alert.
     * @return the identifier for the sensor associated with the custom alert.
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * Sets the identifier for the sensor associated with the custom alert.
     * @param sensorId Identifier for the sensor associated with the custom alert.
     */
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Get the parameter to be monitored for the custom alert.
     * @return the parameter to be monitored for the custom alert.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter to be monitored for the custom alert.
     * @param parameter Parameter to be monitored for the custom alert.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Get the condition that triggers the custom alert.
     * @return the condition that triggers the custom alert.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the condition that triggers the custom alert.
     * @param condition Condition that triggers the custom alert.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Get the threshold value for the custom alert.
     * @return the threshold value for the custom alert.
     */
    public float getThreshold() {
        return threshold;
    }

    /**
     * Sets the threshold value for the custom alert.
     * @param threshold Threshold value for the custom alert.
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Get the message to be sent when the custom alert is triggered.
     * @return the message to be sent when the custom alert is triggered.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message to be sent when the custom alert is triggered.
     * @param message Message to be sent when the custom alert is triggered.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
