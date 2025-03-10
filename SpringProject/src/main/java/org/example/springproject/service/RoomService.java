package org.example.springproject.service;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.entity.Room;

import java.util.List;


public interface RoomService {
    RoomDTO addRoom(Room room) throws RuntimeException;

    RoomDTO deleteRoomById(String id);

    RoomDTO updateRoom(String id, Room updatedRoom);

    List<RoomDTO> getRooms();

    List<RoomDTO> getRoomsByUserId(String id);

    RoomDTO addSensorToRoom(String id, String sensorId);
}
