package org.example.springproject.service;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface RoomService {
    RoomDTO addRoom(Room room) throws RuntimeException;

    RoomDTO deleteRoomById(String id);

    RoomDTO updateRoom(String id, Room updatedRoom);

    List<RoomDTO> getRooms();

    RoomDTO getRoomById(String roomId);

    List<RoomDTO> getAvailableRooms();

    List<RoomDTO> getRoomsByUserId(String id);

    List<RoomDTO> getRoomsByUserEmail(String email);

    RoomDTO addSensorToRoom(String id, String sensorId);

    List<RoomDTO> getRoomsByAuthenticatedUser();

    List<SensorDTO> getSensorsByRoomId(String id);

    String updateRoomWithSensorData(String roomId, SensorDTO sensorDTO) throws ExecutionException, InterruptedException;

    RoomDTO assignRoomToUser(String roomId, String userId, String newName, List<String> selectedSensorIds);

    RoomDTO removeUserFromRoom(String roomId, String userId);
}
