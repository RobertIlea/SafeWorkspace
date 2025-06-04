/**
 * Room.java
 * This class represents the room collection in the Firestore.
 * @Author Ilea Robert-Ioan
 */
package org.example.springproject.entity;

import java.util.List;

/**
 * Represents a room in the system.
 */
public class Room {

    /**
     * The ID of the user who owns the room.
     */
    private String userId;

    /**
     * The list of sensors associated with the room.
     */
    private List<Sensor> sensors;

    /**
     * The name of the room.
     */
    private String name;

    /**
     * Default constructor for Room.
     */
    public Room(){}

    /**
     * Constructs a Room with the specified user ID, sensors, and name.
     * @param userId The ID of the user who owns the room.
     * @param sensors The list of sensors associated with the room.
     * @param name The name of the room.
     */
    public Room(String userId, List<Sensor> sensors, String name){
        this.userId = userId;
        this.sensors = sensors;
        this.name = name;
    }

    /**
     * Gets the user ID of the room.
     * @return The ID of the user who owns the room.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID of the room.
     * @param userId The ID of the user who owns the room.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the list of sensors associated with the room.
     * @return The list of sensors.
     */
    public List<Sensor> getSensors() {
        return sensors;
    }

    /**
     * Sets the list of sensors associated with the room.
     * @param sensors The list of sensors to set.
     */
    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * Gets the name of the room.
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the room.
     * @param name The name of the room to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}
