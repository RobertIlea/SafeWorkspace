/**
 * CustomAlert.java
 * It extends the Alert class to include user-specific alert parameters.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.entity;

/**
 * CustomAlert class represents an alert with additional user-specific parameters.
 */
public class CustomAlert extends Alert {

    /**
     * The ID of the user who created the alert.
     */
    private String userId;

    /**
     * The parameter that the alert is based on (temperature, humidity etc.).
     */
    private String parameter;

    /**
     * The condition that triggers the alert ("greater than", "less than").
     */
    private String condition;

    /**
     * The threshold value that triggers the alert.
     */
    private float threshold;

    /**
     * Default constructor for CustomAlert.
     * It initializes a new instance of the CustomAlert class.
     */
    public CustomAlert() {}

    /**
     * Constructor for CustomAlert with parameters.
     * It initializes a new instance of the CustomAlert class with specified values.
     * There is no need of a timestamp as it will be set automatically by the Alert class.
     * There is no need of a data as it will be set automatically by the Alert class.
     * @param userId The ID of the user who created the alert.
     * @param roomId The ID of the room where the alert is set.
     * @param sensorId The ID of the sensor that triggered the alert.
     * @param sensorType The type of sensor (e.g., temperature, humidity).
     * @param parameter The parameter that the alert is based on.
     * @param condition The condition that triggers the alert.
     * @param threshold The threshold value that triggers the alert.
     * @param message The message associated with the alert.
     */
    public CustomAlert(String userId, String roomId, String sensorId, String sensorType, String parameter, String condition, float threshold, String message) {
        super(roomId, sensorId, null, sensorType, null, message);
        this.userId = userId;
        this.parameter = parameter;
        this.condition = condition;
        this.threshold = threshold;
    }

    /**
     * Get the user ID of the alert.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user ID of the alert.
     * @param userId The ID of the user who created the alert.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the parameter that the alert is based on.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Set the parameter that the alert is based on.
     * @param parameter The parameter that the alert is based on (temperature, humidity etc.).
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Get the condition that triggers the alert.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Set the condition that triggers the alert.
     * @param condition The condition that triggers the alert ("greater than", "less than").
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Get the threshold value that triggers the alert.
     */
    public float getThreshold() {
        return threshold;
    }

    /**
     * Set the threshold value that triggers the alert.
     * @param threshold The threshold value that triggers the alert.
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Method to return a string representation of the CustomAlert object.
     * @return A string representation of the CustomAlert object.
     */
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
