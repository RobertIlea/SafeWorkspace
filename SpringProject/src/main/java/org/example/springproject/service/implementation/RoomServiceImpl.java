package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.RoomService;
import org.example.springproject.util.RoomMapper;
import org.example.springproject.util.SensorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private Firestore firestore;
    private static final String ROOM_COLLECTION = "rooms";
    private static final String USER_COLLECTION = "users";
    private static final String SENSOR_COLLECTION = "sensors";
    private final SensorServiceImpl sensorService;

    @Autowired
    public RoomServiceImpl(Firestore firestore, SensorServiceImpl sensorService) {
        this.firestore = firestore;
        this.sensorService = sensorService;
    }

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

        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding a room: " + e.getMessage(), e);
        }
    }

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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the room: " + e.getMessage(), e);
        }
    }

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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the room: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RoomDTO> getRooms() throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(ROOM_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            return getRoomDTOS(documents);
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the rooms: " + e.getMessage(), e);
        }
    }

    @Override
    public RoomDTO getRoomById(String roomId){
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
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RoomDTO> getAvailableRooms() {
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
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<RoomDTO> getRoomsByUserId(String id){
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(ROOM_COLLECTION).whereEqualTo("userId", id).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            return getRoomDTOS(documents);
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the rooms by userId: " + e.getMessage(), e);
        }
    }
    @Override
    public List<RoomDTO> getRoomsByUserEmail(String email){
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).whereEqualTo("email", email).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if(documents.isEmpty()){
                throw new RuntimeException("User with email: "+ email +" doesn't exist!");
            }
            String userId = documents.get(0).getId();
            return getRoomsByUserId(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public RoomDTO addSensorToRoom(String id, String sensorId){
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
            System.out.println("Sensor id to be updated: " + sensorId);
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

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<RoomDTO> getRoomsByAuthenticatedUser(){
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userId == null){
            throw new RuntimeException("User is not logged in!");
        }
        return getRoomsByUserId(userId);
    }

    @Override
    public List<SensorDTO> getSensorsByRoomId(String id){
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
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String updateRoomWithSensorData(String roomId, SensorDTO sensorDTO) throws ExecutionException, InterruptedException {
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
        System.out.println("Updating room with id: " + roomId);
        System.out.println("Sensor id to be updated: " + sensorDTO.getId());
        roomDTO.setSensors(sensorsDTO);
        WriteResult result = roomRef.set(roomDTO).get();
        return result.getUpdateTime().toString();
    }

    @Override
    public RoomDTO assignRoomToUser(String roomId, String userId, String newName, List<String> selectedSensorIds){
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

        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while assigning the room to the user: " + e.getMessage(), e);
        }
    }

    @Override
    public RoomDTO removeUserFromRoom(String roomId, String userId){
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

            roomRef.update("userId", "").get();

            @SuppressWarnings("unchecked")
            List<Map<String,Object>> sensorsData = (List<Map<String, Object>>) snapshot.get("sensors");

            return RoomMapper.toDTO(roomId,room,sensorsData);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
