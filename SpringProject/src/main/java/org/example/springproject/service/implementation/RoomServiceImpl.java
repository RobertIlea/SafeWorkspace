package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.RoomService;
import org.example.springproject.util.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private Firestore firestore;
    private static final String ROOM_COLLECTION = "rooms";
    private static final String USER_COLLECTION = "users";

    public void roomVerification(Room room){
        if(room.getUserId().isEmpty()){
            throw new RuntimeException("User with id: " + room.getUserId() + " doesn't exist!");
        }
        if(room.getSensors().isEmpty()){
            throw new RuntimeException("Room has no sensors!");
        }
        List<Sensor> sensorList=room.getSensors();
        for(Sensor sensor:sensorList){
            if(sensor.getDetails().getPort() < 1 || sensor.getDetails().getPort() > 40){
                throw new RuntimeException("Sensor port must be between 1 and 40");
            }
            if(sensor.getDetails().getSensorName().isEmpty() || sensor.getDetails().getSensorName().length() > 30) {
                throw new RuntimeException("Length of the sensor name must be below 30");
            }
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
            return new RoomDTO(roomRef.getId(),room.getSensors());
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
            return new RoomDTO(id,room.getSensors());
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
            return new RoomDTO(id,currentRoom.getSensors());
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
}
