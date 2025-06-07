/**
 * SensorDTO.java
 * This class represents a Data Transfer Object (DTO) for a sensor in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.dto;

import org.example.springproject.entity.Details;

import java.util.List;

/**
 * SensorDTO is used to transfer sensor data between different layers of the application.
 */
public class SensorDTO {

    /**
     * Unique identifier for the sensor.
     */
    private String id;

    /**
     * Type of the sensor.
     */
    private String sensorType;

    /**
     * Port number associated with the sensor.
     */
    private Integer port;

    /**
     * List of details associated with the sensor.
     */
    private List<Details> details;

    /**
     * Indicates whether the sensor is active.
     */
    private boolean active = false;

    /**
     * Default constructor for SensorDTO.
     */
    public SensorDTO(){}

    /**
     * Parameterized constructor for SensorDTO.
     * @param id Unique identifier for the sensor.
     * @param sensorType Type of the sensor.
     * @param port Port number associated with the sensor.
     * @param details List of details associated with the sensor.
     */
    public SensorDTO(String id, String sensorType,Integer port, List<Details> details, boolean active) {
        this.id = id;
        this.sensorType = sensorType;
        this.port = port;
        this.details = details;
        this.active = active;
    }

    public SensorDTO(String id, String sensorType, int i, List<Details> details) {
        this.id = id;
        this.sensorType = sensorType;
        this.port = i;
        this.details = details;
    }

    /**
     * Gets the unique identifier for the sensor.
     * @return the unique identifier of the sensor.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the sensor.
     * @param id the unique identifier to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the port number associated with the sensor.
     * @return the port number of the sensor.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port number associated with the sensor.
     * @param port the port number to set.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the type of the sensor.
     * @return the type of the sensor.
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * Sets the type of the sensor.
     * @param sensorType the type of the sensor to set.
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Gets the list of details associated with the sensor.
     * @return the list of details.
     */
    public List<Details> getDetails() {
        return details;
    }

    /**
     * Sets the list of details associated with the sensor.
     * @param details the list of details to set.
     */
    public void setDetails(List<Details> details) {
        this.details = details;
    }

    /**
     * Checks if the sensor is active.
     * @return true if the sensor is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the sensor.
     * @param active true to set the sensor as active, false otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns a string representation of the SensorDTO object.
     * @return the string representation of the SensorDTO.
     */
    @Override
    public String toString() {
        return "SensorDTO{" +
                "id='" + id + '\'' +
                ", sensorType='" + sensorType + '\'' +
                ", port=" + port +
                ", details=" + details +
                ", isActive=" + active +
                '}';
    }
}
