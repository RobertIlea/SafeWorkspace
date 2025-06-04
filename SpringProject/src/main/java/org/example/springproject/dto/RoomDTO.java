/**
 * RoomDTO.java
 * This class represents a Data Transfer Object (DTO) for a room in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.dto;

import java.util.List;

/**
 * RoomDTO is used to transfer room data between different layers of the application.
 */
public class RoomDTO {

    /**
     * Unique identifier for the room.
     */
    private String id;

    /**
     * List of sensors associated with the room.
     */
    private List<SensorDTO> sensors;

    /**
     * Name of the room.
     */
    private String name;

    /**
     * Identifier for the user who owns the room.
     */
    private String userId;

    /**
     * Default constructor for RoomDTO.
     */
    public RoomDTO() {}

    /**
     * Parameterized constructor for RoomDTO.
     * @param id Unique identifier for the room.
     * @param sensors List of sensors associated with the room.
     * @param name Name of the room.
     * @param userId Identifier for the user who owns the room.
     */
    public RoomDTO(String id, List<SensorDTO> sensors, String name, String userId) {
        this.id = id;
        this.sensors = sensors;
        this.name = name;
        this.userId = userId;
    }

    /**
     * Gets the unique identifier for the room.
     * @return the unique identifier of the room.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the room.
     * @param id the unique identifier to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the list of sensors associated with the room.
     * @return the list of sensors.
     */
    public List<SensorDTO> getSensors() {
        return sensors;
    }

    /**
     * Sets the list of sensors associated with the room.
     * @param sensors the list of sensors to set.
     */
    public void setSensors(List<SensorDTO> sensors) {
        this.sensors = sensors;
    }

    /**
     * Gets the name of the room.
     * @return the name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the room.
     * @param name the name to set for the room.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the identifier for the user who owns the room.
     * @return the user identifier.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the identifier for the user who owns the room.
     * @param userId the user identifier to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns a string representation of the RoomDTO object.
     * @return a string containing the room id, sensors, name, and user id.
     */
    @Override
    public String toString(){
        return "room id: " + id + " Senzori: " + sensors.toString() + " nume camera: " + name + " user id: " + userId;
    }
}
