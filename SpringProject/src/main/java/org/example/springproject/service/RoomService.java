/**
 * RoomService.java
 * This interface defines the contract for room-related operations in the Spring project.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * RoomService interface provides methods to manage rooms, including adding, deleting, updating, and retrieving room information, as well as managing sensors associated with rooms.
 */
public interface RoomService {

    /**
     * Adds a new room to the system.
     * @param room The room to be added.
     * @return The added room as a RoomDTO.
     * @throws RuntimeException if an error occurs while adding the room.
     */
    RoomDTO addRoom(Room room) throws RuntimeException;

    /**
     * Deletes a room by its ID.
     * @param id The ID of the room to be deleted.
     * @return The deleted room as a RoomDTO.
     */
    RoomDTO deleteRoomById(String id);

    /**
     * Updates an existing room.
     * @param id The ID of the room to be updated.
     * @param updatedRoom The updated room information.
     * @return The updated room as a RoomDTO.
     */
    RoomDTO updateRoom(String id, Room updatedRoom);

    /**
     * Retrieves a room by its ID.
     * @param roomId The ID of the room to be retrieved.
     * @return The room as a RoomDTO.
     */
    RoomDTO getRoomById(String roomId);

    /**
     * Retrieves all available rooms.
     * @return A list of available rooms as RoomDTOs.
     */
    List<RoomDTO> getAvailableRooms();

    /**
     * Retrieves all rooms associated with a specific user ID.
     * @param id The ID of the user.
     * @return A list of rooms associated with the user as RoomDTOs.
     */
    List<RoomDTO> getRoomsByUserId(String id);

    /**
     * Retrieves all rooms associated with a specific user email.
     * @param email The email of the user.
     * @return A list of rooms associated with the user's email as RoomDTOs.
     */
    List<RoomDTO> getRoomsByUserEmail(String email);

    /**
     * Adds a sensor to a room.
     * @param id The ID of the room.
     * @param sensorId The ID of the sensor to be added.
     * @return The updated room as a RoomDTO.
     */
    RoomDTO addSensorToRoom(String id, String sensorId);

    /**
     * Gets all sensors associated with a specific room ID.
     * @param id The ID of the room.
     * @return A list of sensors associated with the room as SensorDTOs.
     */
    List<SensorDTO> getSensorsByRoomId(String id);

    /**
     * Updates a room with sensor data.
     * @param roomId The ID of the room to be updated.
     * @param sensorDTO The sensor data to be added to the room.
     * @return The updated room ID as a String.
     * @throws ExecutionException if an error occurs during the update process.
     * @throws InterruptedException if the thread is interrupted while waiting for the update to complete.
     */
    String updateRoomWithSensorData(String roomId, SensorDTO sensorDTO) throws ExecutionException, InterruptedException;

    /**
     * Assigns a room to a user with the option to select sensors.
     * @param roomId The ID of the room to be assigned.
     * @param userId The ID of the user to whom the room is assigned.
     * @param newName The new name for the room.
     * @param selectedSensorIds A list of sensor IDs to be associated with the room.
     * @return The updated room as a RoomDTO.
     */
    RoomDTO assignRoomToUser(String roomId, String userId, String newName, List<String> selectedSensorIds);

    /**
     * Removes a user from a room.
     * @param roomId The ID of the room from which the user is to be removed.
     * @param userId The ID of the user to be removed.
     * @return The updated room as a RoomDTO.
     */
    RoomDTO removeUserFromRoom(String roomId, String userId);
}
