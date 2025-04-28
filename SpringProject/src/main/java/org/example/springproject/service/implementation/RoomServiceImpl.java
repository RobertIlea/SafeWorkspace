package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.checkerframework.checker.units.qual.N;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
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
    public void roomVerification(Room room){
        if(room.getUserId().isEmpty()) {
            throw new RuntimeException("User with id: " + room.getUserId() + " doesn't exist!");
        }
    }
    private List<RoomDTO> getRoomDTOS(List<QueryDocumentSnapshot> documents) {
        List<RoomDTO> rooms = new ArrayList<>();

        for(QueryDocumentSnapshot document:documents){
            Room room = document.toObject(Room.class);
            RoomDTO roomDTO = RoomMapper.toDTO(document.getId(), room);
            rooms.add(roomDTO);
            System.out.println("camera: " + roomDTO.toString());
        }
        return rooms;
    }
    @Override
    public RoomDTO addRoom(Room room) throws RuntimeException {
        try{
            roomVerification(room);

            DocumentReference userRef = firestore.collection(USER_COLLECTION).document(room.getUserId());
            DocumentSnapshot userSnapshot = userRef.get().get();

            if(!userSnapshot.exists()){
                throw new RuntimeException("User with id: " + room.getUserId() + " doesn't exist!");
            }

            DocumentReference roomRef = firestore.collection(ROOM_COLLECTION).document();
            roomRef.set(room).get();
            return new RoomDTO(roomRef.getId(),SensorMapper.toDTOList(room.getSensors()),room.getName(),room.getUserId());
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
            return new RoomDTO(id,SensorMapper.toDTOList(room.getSensors()),room.getName(),room.getUserId());
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

            roomVerification(updatedRoom);

            Room currentRoom = roomSnapshot.toObject(Room.class);
            assert currentRoom != null;
            currentRoom.setSensors(updatedRoom.getSensors());
            roomRef.set(currentRoom).get();
            return new RoomDTO(id,SensorMapper.toDTOList(currentRoom.getSensors()), currentRoom.getName(),currentRoom.getUserId());
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

        roomDTO.setSensors(sensorsDTO);

        WriteResult result = roomRef.set(roomDTO).get();

        return result.getUpdateTime().toString();
    }

}
