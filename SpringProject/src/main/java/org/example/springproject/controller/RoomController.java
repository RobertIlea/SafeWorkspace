/**
 * RoomController.java
 * The file represents the REST controller for managing room-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.EmptyResultException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RoomController handles HTTP requests related to room operations.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/room" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/room")
public class RoomController {

    /**
     * The RoomService is injected to handle business logic related to rooms.
     */
    @Autowired
    private RoomService roomService;

    /**
     * The JwtService is injected to handle JWT token operations.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * This method handles GET requests to retrieve all rooms for a user based on their email extracted from the JWT token.
     * @param token the JWT token containing the user's email
     * @return ResponseEntity containing a list of RoomDTO objects
     * @throws ObjectNotFound if the room list is empty or not found for the user
     */
    @GetMapping("/")
    public ResponseEntity<List<RoomDTO>> getRooms(@RequestHeader("Authorization") String token) throws ObjectNotFound, EmptyResultException {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String email = jwtService.extractEmail(jwtToken);

        if (email == null || email.isEmpty()) {
            throw new ObjectNotFound("Email not found in token");
        }

        List<RoomDTO> rooms = roomService.getRoomsByUserEmail(email);

        if (rooms == null || rooms.isEmpty()) {
            throw new EmptyResultException("Room list is empty or not found for user with email: " + email);
        }

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve sensors by room ID.
     * @param roomId the ID of the room for which sensors are to be retrieved
     * @return ResponseEntity containing a list of SensorDTO objects
     * @throws ObjectNotFound if no sensors are found for the specified room ID
     */
    @GetMapping("{roomId}/sensors")
    public ResponseEntity<List<SensorDTO>> getSensorsByRoomId(@PathVariable String roomId) throws ObjectNotFound {
        List<SensorDTO> sensorDTOS = roomService.getSensorsByRoomId(roomId);

        if(sensorDTOS == null || sensorDTOS.isEmpty()) {
            throw new ObjectNotFound("No sensors found for room with ID: " + roomId);
        }

        return new ResponseEntity<>(sensorDTOS, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve all available rooms.
     * @return ResponseEntity containing a list of available RoomDTO objects
     * @throws EmptyResultException if no available rooms are found
     */
    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() throws EmptyResultException {
        List<RoomDTO> rooms = roomService.getAvailableRooms();

        if(rooms == null || rooms.isEmpty()) {
            throw new EmptyResultException("No available rooms found!");
        }

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve rooms by user ID.
     * @param id the ID of the user for whom rooms are to be retrieved
     * @return ResponseEntity containing a list of RoomDTO objects
     * @throws ObjectNotFound if no rooms are found for the specified user ID
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<RoomDTO>> getRoomsByUserId(@PathVariable String id) throws ObjectNotFound {
        List<RoomDTO> rooms = roomService.getRoomsByUserId(id);

        if(rooms == null || rooms.isEmpty()) {
            throw new ObjectNotFound("No rooms found for user with ID: " + id);
        }

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve a room by its ID.
     * @param roomId the ID of the room to retrieve
     * @return ResponseEntity containing the RoomDTO object
     * @throws ObjectNotFound if the room with the specified ID is not found
     */
    @GetMapping("{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable String roomId) throws ObjectNotFound {
        RoomDTO roomDTO = roomService.getRoomById(roomId);

        if(roomDTO == null) {
            throw new ObjectNotFound("Room with ID " + roomId + " not found!");
        }

        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    /**
     * This method handles POST requests to add a new room.
     * @param room the Room object to be added
     * @return ResponseEntity containing the created RoomDTO object
     * @throws CreationException if the room could not be created due to being null
     */
    @PostMapping("/")
    public ResponseEntity<RoomDTO> addRoom(@RequestBody Room room) throws CreationException {
        RoomDTO roomDTO = roomService.addRoom(room);

        if(roomDTO == null) {
            throw new CreationException("Failed to create room because it's null!");
        }

        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles DELETE requests to remove a room by its ID.
     * @param id the ID of the room to delete
     * @return ResponseEntity containing the deleted RoomDTO object
     * @throws ObjectNotFound if the room with the specified ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RoomDTO> deleteRoomById(@PathVariable String id) throws ObjectNotFound {
        RoomDTO roomDTO = roomService.deleteRoomById(id);

        if(roomDTO == null) {
            throw new ObjectNotFound("Room with ID " + id + " not found!");
        }

        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    /**
     * This method handles DELETE requests to remove a user from a room by their user ID.
     * @param roomId the ID of the room from which the user is to be removed
     * @param userId the ID of the user to be removed from the room
     * @return ResponseEntity containing the updated RoomDTO object
     * @throws ObjectNotFound if the room with the specified ID is not found or if the user is not assigned to this room
     */
    @DeleteMapping("/{roomId}/remove/{userId}")
    public ResponseEntity<RoomDTO> removeRoomById(@PathVariable String roomId, @PathVariable String userId) throws ObjectNotFound {
        RoomDTO roomDTO = roomService.removeUserFromRoom(roomId, userId);

        if(roomDTO == null) {
            throw new ObjectNotFound("Room with ID " + roomId + " not found or user with ID " + userId + " is not assigned to this room!");
        }

        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    /**
     * This method handles PUT requests to update an existing room by its ID.
     * @param id the ID of the room to update
     * @param room the Room object containing updated information
     * @return ResponseEntity containing the updated RoomDTO object
     * @throws ObjectNotFound if the room with the specified ID is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable String id, @RequestBody Room room) throws ObjectNotFound {
        RoomDTO roomDTO = roomService.updateRoom(id,room);

        if(roomDTO == null) {
            throw new ObjectNotFound("Room with ID " + id + " not found!");
        }

        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    /**
     * This method handles POST requests to add a sensor to a room by its ID.
     * @param id the ID of the room to which the sensor is to be added
     * @param requestBody the request body containing the sensor ID
     * @return ResponseEntity containing the updated RoomDTO object
     * @throws CreationException if the sensor ID is not found in the request body or if the room is null
     */
    @PostMapping("/test/{id}")
    public ResponseEntity<RoomDTO> addSensorToRoom(@PathVariable String id, @RequestBody Map<String, String> requestBody) throws CreationException{
            String sensorId = requestBody.get("sensorId");

            if (sensorId == null) {
                throw new CreationException("No sensor id found!");
            }

            RoomDTO roomDTO = roomService.addSensorToRoom(id,sensorId);

            if(roomDTO == null) {
                throw new CreationException("Failed to add sensor to room because room is null!");
            }

            return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles POST requests to assign a room to a user.
     * @param requestBody the request body containing roomId, userId, roomName, and sensorIds
     * @return ResponseEntity containing the updated RoomDTO object
     * @throws CreationException if the roomId or userId is null, or if the room could not be assigned
     */
    @PostMapping("/assign")
    public ResponseEntity<RoomDTO> assignRoomToUser(@RequestBody Map<String, Object> requestBody) throws CreationException {
            String roomId = (String) requestBody.get("roomId");
            String userId = (String) requestBody.get("userId");
            String roomName = (String) requestBody.get("roomName");

            // Convert sensorIds from List<Object> to List<String>
            List<String> sensorIds = ((List<?>) requestBody.get("sensorIds")).stream().map(Object::toString).collect(Collectors.toList());

            if (roomId == null || userId == null) {
                throw new CreationException("Room ID and User ID must not be null!");
            }

            RoomDTO roomDTO = roomService.assignRoomToUser(roomId,userId,roomName,sensorIds);

            if(roomDTO == null) {
                throw new CreationException("Failed to assign room to user because room is null!");
            }

            return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

}
