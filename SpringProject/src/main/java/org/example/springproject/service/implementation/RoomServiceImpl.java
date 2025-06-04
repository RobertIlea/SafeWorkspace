/**
 * RoomServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the methods from RoomService interface.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.RoomService;
import org.example.springproject.util.RoomMapper;
import org.example.springproject.util.SensorMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * RoomServiceImpl is a service class that implements the RoomService interface.
 * It provides methods to manage rooms in the Firestore database.
 */
@Service
public class RoomServiceImpl implements RoomService {

    /**
     * Firestore instance used to interact with the Firestore database.
     */
    private final Firestore firestore;

    /**
     * The name of the collection in Firestore where rooms are stored.
     */
    private static final String ROOM_COLLECTION = "rooms";

    /**
     * The name of the collection in Firestore where users are stored.
     */
    private static final String USER_COLLECTION = "users";

    /**
     * The name of the collection in Firestore where sensors are stored.
     */
    private static final String SENSOR_COLLECTION = "sensors";

    /**
     * SensorService instance used to interact with sensors.
     */
    private final SensorServiceImpl sensorService;

    /**
     * Constructor for RoomServiceImpl.
     * @param firestore Firestore instance used to interact with the Firestore database.
     * @param sensorService SensorServiceImpl instance used to interact with sensors.
     */
    public RoomServiceImpl(Firestore firestore, SensorServiceImpl sensorService) {
        this.firestore = firestore;
        this.sensorService = sensorService;
    }

    /**
     * Converts a list of QueryDocumentSnapshots to a list of RoomDTOs.
     * @param roomDocs List of QueryDocumentSnapshots representing rooms.
     * @return List of RoomDTOs.
     */
    private List<RoomDTO> getRoomDTOS(List<QueryDocumentSnapshot> roomDocs) {
        return roomDocs.stream().map(doc -> {
            Room room = doc.toObject(Room.class);

            // Extract sensors as raw maps (preserves IDs)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> sensorMaps = (List<Map<String, Object>>) doc.get("sensors");
            System.out.println("Sensors maps: " + sensorMaps);
            return RoomMapper.toDTO(doc.getId(), room, sensorMaps);
        }).toList();
    }

    /**
     * Adds a new room to the Firestore database.
     * @param room The room to be added.
     * @return A RoomDTO object containing the details of the added room.
     * @throws RuntimeException if there is an error while adding the room.
     */
    @Override
    public RoomDTO addRoom(Room room) throws RuntimeException {
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document();
            DocumentSnapshot roomSnapshot = roomRef.get().get();
            if(roomSnapshot.exists()){
                throw new RuntimeException("Room with id: "+ roomRef.getId() +" already exists!");
            }

            ApiFuture<WriteResult> future = roomRef.set(room);
            future.get();

            return new RoomDTO(roomRef.getId(), SensorMapper.toDTOList(room.getSensors()), room.getName(), room.getUserId());

        } catch (Exception e) {
            throw new RuntimeException("Error while adding a room: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a room by its ID from the Firestore database.
     * @param id The ID of the room to be deleted.
     * @return A RoomDTO object containing the details of the deleted room.
     * @throws RuntimeException if there is an error while deleting the room or if the room does not exist.
     */
    @Override
    public RoomDTO deleteRoomById(String id) throws RuntimeException{
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(id);
            DocumentSnapshot roomSnapshot = roomRef.get().get();

            if(!roomSnapshot.exists()){
                throw new RuntimeException("Room with id: "+ id +" doesn't exist!");
            }
            Room room = roomSnapshot.toObject(Room.class);
            roomRef.delete().get();

            assert room != null;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) roomSnapshot.get("sensors");
            return new RoomDTO(id,SensorMapper.toDTOListMap(mapList),room.getName(),room.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the room: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a room by its ID in the Firestore database.
     * @param id The ID of the room to be updated.
     * @param updatedRoom The updated room object.
     * @return A RoomDTO object containing the details of the updated room.
     * @throws RuntimeException if there is an error while updating the room or if the room does not exist.
     */
    @Override
    public RoomDTO updateRoom(String id, Room updatedRoom) throws RuntimeException{
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(id);
            DocumentSnapshot roomSnapshot = roomRef.get().get();

            if(!roomSnapshot.exists()){
                throw new RuntimeException("Room with id: " + id + " doesn't exist!");
            }

            Room currentRoom = roomSnapshot.toObject(Room.class);
            assert currentRoom != null;
            currentRoom.setSensors(updatedRoom.getSensors());
            roomRef.set(currentRoom).get();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) roomSnapshot.get("sensors");
            return new RoomDTO(id,SensorMapper.toDTOListMap(mapList), currentRoom.getName(),currentRoom.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the room: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a room by its ID from the Firestore database.
     * @param roomId The ID of the room to be retrieved.
     * @return A RoomDTO object containing the details of the retrieved room.
     * @throws RuntimeException if there is an error while retrieving the room or if the room does not exist.
     */
    @Override
    public RoomDTO getRoomById(String roomId) throws RuntimeException{
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(roomId);
            DocumentSnapshot roomSnapshot = roomRef.get().get();

            if(!roomSnapshot.exists()){
                throw new RuntimeException("Room with id: "+ roomId +" doesn't exist!");
            }
            Room room = roomSnapshot.toObject(Room.class);
            assert room != null;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) roomSnapshot.get("sensors");
            return new RoomDTO(roomId,SensorMapper.toDTOListMap(mapList),room.getName(),room.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the room by id: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the available rooms from the Firestore database.
     * @return A list of RoomDTO objects containing the details of all the available rooms.
     * @throws RuntimeException if there is an error while retrieving the available rooms.
     */
    @Override
    public List<RoomDTO> getAvailableRooms() throws RuntimeException{
        // Rooms that are not assigned to any user
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(ROOM_COLLECTION).whereEqualTo("userId", "").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<RoomDTO> roomDTOS = getRoomDTOS(documents);
            List<RoomDTO> availableRooms = new ArrayList<>();
            for (RoomDTO roomDTO : roomDTOS) {
                if(roomDTO.getUserId().isEmpty() || roomDTO.getUserId() == null){
                    roomDTO.setUserId(null);
                    availableRooms.add(roomDTO);
                }
            }

            return availableRooms;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the available rooms: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the rooms assigned to a specific user by their user ID.
     * @param id The ID of the user for whom the rooms are to be retrieved.
     * @return A list of RoomDTO objects containing the details of the rooms assigned to the user.
     * @throws RuntimeException if there is an error while retrieving the rooms or if the user does not exist.
     */
    @Override
    public List<RoomDTO> getRoomsByUserId(String id) throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(ROOM_COLLECTION).whereEqualTo("userId", id).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            return getRoomDTOS(documents);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the rooms by userId: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the rooms assigned to a specific user by their email.
     * @param email The email of the user for whom the rooms are to be retrieved.
     * @return A list of RoomDTO objects containing the details of the rooms assigned to the user.
     * @throws RuntimeException if there is an error while retrieving the rooms or if the user does not exist.
     */
    @Override
    public List<RoomDTO> getRoomsByUserEmail(String email) throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).whereEqualTo("email", email).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if(documents.isEmpty()){
                throw new RuntimeException("User with email: "+ email +" doesn't exist!");
            }
            String userId = documents.get(0).getId();
            return getRoomsByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the rooms by user email: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a sensor to a room by its ID.
     * @param id The ID of the room to which the sensor is to be added.
     * @param sensorId The ID of the sensor to be added to the room.
     * @return A RoomDTO object containing the details of the updated room.
     * @throws RuntimeException if there is an error while adding the sensor or if the room or sensor does not exist.
     */
    @Override
    public RoomDTO addSensorToRoom(String id, String sensorId) throws RuntimeException{
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(id);
            DocumentSnapshot roomSnapshot = roomRef.get().get();

            if(!roomSnapshot.exists()){
                throw new RuntimeException("Room with id: "+ id +" doesn't exist!");
            }

            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(sensorId);
            DocumentSnapshot sensorSnapshot = sensorRef.get().get();

            if(!sensorSnapshot.exists()){
                throw new RuntimeException("Sensor with id: "+ sensorId +" doesn't exist!");
            }
            Sensor sensor = sensorService.deserializeSensor(sensorSnapshot);

            SensorDTO sensorDTO = SensorMapper.toDTO(sensorId,sensor);
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> sensorsListMap = (List<Map<String, Object>>) roomSnapshot.get("sensors");
            List<SensorDTO> sensorsList = new ArrayList<>();
            if (sensorsListMap != null) {
                for (Map<String,Object> sensorMap : sensorsListMap) {
                    SensorDTO newSensorDTO = new SensorDTO();
                    newSensorDTO.setId((String) sensorMap.get("id"));
                    newSensorDTO.setSensorType((String) sensorMap.get("sensorType"));

                    Object portObj = sensorMap.get("port");
                    Integer port = null;
                    if(portObj instanceof Number){
                        port = ((Number) portObj).intValue();
                    }
                    newSensorDTO.setPort(port);
                    newSensorDTO.setDetails((List<Details>) sensorMap.get("details"));
                    sensorsList.add(newSensorDTO);
                }
            }

            sensorsList.add(sensorDTO);
            roomRef.update("sensors", sensorsList).get();

            return new RoomDTO(id,sensorsList, roomSnapshot.getString("name"), roomSnapshot.getString("userId"));

        } catch (Exception e) {
            throw new RuntimeException("Error while adding sensor to room: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the sensors associated with a specific room by its ID.
     * @param id The ID of the room for which the sensors are to be retrieved.
     * @return A list of SensorDTO objects containing the details of the sensors associated with the room.
     * @throws RuntimeException if there is an error while retrieving the sensors or if the room does not exist.
     */
    @Override
    public List<SensorDTO> getSensorsByRoomId(String id) throws RuntimeException{
        try{
            ApiFuture<DocumentSnapshot> future = firestore.collection(ROOM_COLLECTION).document(id).get();
            DocumentSnapshot documentSnapshot = future.get();

            if(!documentSnapshot.exists()){
                throw new RuntimeException("Room with id " + id + " doesn't exists!");
            }

            @SuppressWarnings("unchecked")
            List<Map<String,Object>> sensorsData = (List<Map<String, Object>>) documentSnapshot.get("sensors");

            List<SensorDTO> sensorDTOs = new ArrayList<>();
            if(sensorsData != null){
                for(Map<String,Object> sensorData: sensorsData){
                    SensorDTO sensorDTO = new SensorDTO();
                    sensorDTO.setId((String) sensorData.get("id"));
                    sensorDTO.setSensorType((String) sensorData.get("sensorType"));

                    Object portObj = sensorData.get("port");
                    Integer port = null;
                    if(portObj instanceof Number){
                        port = ((Number) portObj).intValue();
                    }
                    sensorDTO.setPort(port);
                    sensorDTO.setDetails((List<Details>) sensorData.get("details"));
                    sensorDTOs.add(sensorDTO);
                }
            }
            return sensorDTOs;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching sensors by room id: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a room with sensor data by its ID.
     * @param roomId The ID of the room to be updated.
     * @param sensorDTO The SensorDTO object containing the sensor data to be updated.
     * @return A string representing the update time of the room.
     * @throws RuntimeException if there is an error while updating the room with sensor data.
     */
    @Override
    public String updateRoomWithSensorData(String roomId, SensorDTO sensorDTO) throws RuntimeException {
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(roomId);
            ApiFuture<DocumentSnapshot> future = roomRef.get();
            DocumentSnapshot roomSnapshot = future.get();

            if (!roomSnapshot.exists()) {
                throw new RuntimeException("Room with id: " + roomId + " doesn't exist!");
            }

            RoomDTO roomDTO = roomSnapshot.toObject(RoomDTO.class);
            if(roomDTO == null){
                throw new RuntimeException("Failed to parse room data!");
            }

            List<SensorDTO> sensorsDTO = roomDTO.getSensors();

            for (SensorDTO sensor : sensorsDTO) {
                if (sensor.getId() != null && sensor.getId().equals(sensorDTO.getId())) {
                    sensor.setDetails(List.of(sensorDTO.getDetails().get(0)));
                }
            }

            roomDTO.setSensors(sensorsDTO);
            WriteResult result = roomRef.set(roomDTO).get();
            return result.getUpdateTime().toString();
        }catch (Exception e){
            throw new RuntimeException("Error while updating room with sensor data: " + e.getMessage(), e);
        }
    }

    /**
     * Assigns a room to a user by updating the room's userId and name, and adding selected sensors.
     * @param roomId The ID of the room to be assigned.
     * @param userId The ID of the user to whom the room is being assigned.
     * @param newName The new name for the room.
     * @param selectedSensorIds The list of sensor IDs to be associated with the room.
     * @return A RoomDTO object containing the details of the updated room.
     * @throws RuntimeException if there is an error while assigning the room or if the room is already assigned.
     */
    @Override
    public RoomDTO assignRoomToUser(String roomId, String userId, String newName, List<String> selectedSensorIds) throws RuntimeException{
        RoomDTO roomDTO = getRoomById(roomId);
        if(roomDTO.getUserId().isEmpty()){
            roomDTO.setUserId(null);
        }
        if(roomDTO.getUserId() != null){
            throw new RuntimeException("Room with id: " + roomId + " already assigned!");
        }

        if(selectedSensorIds == null){
            throw new RuntimeException("Selected sensor ids cannot be null!");
        }
        // Create a list of the selected sensors
        List<SensorDTO> selectedSensors = new ArrayList<>();
        for(String sensorId: selectedSensorIds) {
            SensorDTO sensorDTO = sensorService.getSensorById(sensorId);
            if(sensorDTO == null){
                throw new RuntimeException("Sensor with id: " + sensorId + " doesn't exist!");
            }
            selectedSensors.add(sensorDTO);
        }

        // Update the room with the new userId and name
        roomDTO.setUserId(userId);
        roomDTO.setName(newName);
        roomDTO.setSensors(selectedSensors);

        try {
            // Update the room in Firestore
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(roomId);
            roomRef.set(roomDTO).get();

            return roomDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to assign the room to the user: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a user from a room by setting the userId to an empty string.
     * @param roomId The ID of the room from which the user is to be removed.
     * @param userId The ID of the user to be removed from the room.
     * @return A RoomDTO object containing the details of the updated room.
     * @throws RuntimeException if there is an error while removing the user or if the room does not exist.
     */
    @Override
    public RoomDTO removeUserFromRoom(String roomId, String userId) throws RuntimeException{
        try{
            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document(roomId);
            ApiFuture<DocumentSnapshot> future = roomRef.get();
            DocumentSnapshot snapshot = future.get();
            if(!snapshot.exists()){
                throw new RuntimeException("Room with id: " + roomId + " doesn't exists!");
            }
            Room room = snapshot.toObject(Room.class);
            if(!userId.equals(room.getUserId())){
                throw new RuntimeException("User does not own this room!");
            }

            room.setUserId("");
            roomRef.update("userId", "").get();

            @SuppressWarnings("unchecked")
            List<Map<String,Object>> sensorsData = (List<Map<String, Object>>) snapshot.get("sensors");

            return RoomMapper.toDTO(roomId,room,sensorsData);
        } catch (Exception e) {
            throw new RuntimeException("Error while removing user from room: " + e.getMessage(), e);
        }
    }
}
