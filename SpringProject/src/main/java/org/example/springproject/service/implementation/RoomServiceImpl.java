package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.RoomService;
import org.example.springproject.service.SensorService;
import org.example.springproject.util.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
            RoomDTO roomDTO = RoomMapper.toDTO(room.getUserId(),room);
            rooms.add(roomDTO);
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
            return new RoomDTO(roomRef.getId(),room.getSensors(),room.getName());
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
            return new RoomDTO(id,room.getSensors(),room.getName());
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
            return new RoomDTO(id,currentRoom.getSensors(), currentRoom.getName());
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

            @SuppressWarnings("unchecked")
            List<Sensor> sensorsList = (List<Sensor>) roomSnapshot.get("sensors");
            if (sensorsList == null) {
                sensorsList = new ArrayList<>();
            }

            sensorsList.add(sensor);
            roomRef.update("sensors", sensorsList).get();

            return new RoomDTO(id,sensorsList, roomSnapshot.getString("name"));

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
