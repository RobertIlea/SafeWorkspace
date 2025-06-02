/**
 * RoomMapper.java
 * This class provides methods to convert between Room entity and RoomDTO.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.example.springproject.dto.RoomDTO;

import org.example.springproject.entity.Room;

import java.util.List;
import java.util.Map;

/**
 * RoomMapper is a utility class that maps between Room entities and RoomDTOs.
 * It provides methods to convert a Room entity to a RoomDTO and vice versa.
 */
public class RoomMapper {

    /**
     * Converts a Room entity to a RoomDTO.
     * @param roomId      the ID of the room
     * @param room        the Room entity to convert
     * @param sensorMaps  a list of maps representing sensors associated with the room
     * @return a RoomDTO containing the room's details and associated sensors
     */
    public static RoomDTO toDTO(String roomId, Room room, List<Map<String, Object>> sensorMaps) {
        if (room == null) return null;

        return new RoomDTO(
                roomId,
                SensorMapper.toDTOListMap(sensorMaps),
                room.getName(),
                room.getUserId()
        );
    }

    /**
     * Converts a RoomDTO to a Room entity.
     * @param roomDTO the RoomDTO to convert
     * @return a Room entity containing the details from the RoomDTO
     */
    public static Room toEntity(RoomDTO roomDTO) {
        if (roomDTO == null) {
            return null;
        }
        return new Room(roomDTO.getId(), SensorMapper.toEntityList(roomDTO.getSensors()), roomDTO.getName());
    }
}
