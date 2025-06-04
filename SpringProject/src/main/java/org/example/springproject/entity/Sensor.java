/**
 * Sensor.java
 * This class represents the sensor collection in the Firestore.
 * @Author Ilea Robert-Ioan
 */
package org.example.springproject.entity;

import java.util.List;

/**
 * Represents a sensor in the system.
 */
public class Sensor {

    /**
     * The type of the sensor.
     */
    private String sensorType;

    /**
     * The port number of the sensor.
     */
    private Integer port;

    /**
     * The list of details associated with the sensor.
     */
    private List<Details> details;

    /**
     * Default constructor for Sensor.
     */
    public Sensor(){}

    /**
     * Constructs a Sensor with the specified sensor type, port, and details.
     * @param sensorType The type of the sensor.
     * @param port The port number of the sensor.
     * @param details The list of details associated with the sensor.
     */
    public Sensor(String sensorType, Integer port, List<Details> details) {
        this.sensorType = sensorType;
        this.port = port;
        this.details = details;
    }

    /**
     * Gets the type of the sensor.
     * @return The type of the sensor.
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * Sets the type of the sensor.
     * @param sensorType The type of the sensor.
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Gets the port number of the sensor.
     * @return The port number of the sensor.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port number of the sensor.
     * @param port The port number of the sensor.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the list of details associated with the sensor.
     * @return The list of details.
     */
    public List<Details> getDetails() {
        return details;
    }

    /**
     * Sets the list of details associated with the sensor.
     * @param details The list of details to set.
     */
    public void setDetails(List<Details> details) {
        this.details = details;
    }

    /**
     * Returns a string representation of the Sensor object.
     * @return A string containing the representation of the sensor.
     */
    @Override
    public String toString() {
        return "Sensor{" +
                "sensorType='" + sensorType + '\'' +
                ", port=" + port +
                ", details=" + details +
                '}';
    }
}
