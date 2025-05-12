package org.example.springproject.util;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.User;

import java.util.List;
import java.util.Map;

public class RoomMapper {
    public static RoomDTO toDTO(String roomId, Room room, List<Map<String, Object>> sensorMaps) {
        if (room == null) return null;

        return new RoomDTO(
                roomId,
                SensorMapper.toDTOListMap(sensorMaps),
                room.getName(),
                room.getUserId()
        );
    }

    public static Room toEntity(RoomDTO roomDTO) {
        if (roomDTO == null) {
            return null;
        }
        return new Room(roomDTO.getId(), SensorMapper.toEntityList(roomDTO.getSensors()), roomDTO.getName());
    }
}
