package org.example.springproject.util;

import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.User;

public class RoomMapper {
    public static Room toEntity(RoomDTO roomDTO){
        if(roomDTO == null){
            return null;
        }
        return new Room(roomDTO.getId(),roomDTO.getSensors(), roomDTO.getName());
    }
    public static RoomDTO toDTO(String id, Room room){
        if(room == null){
            return null;
        }
        return new RoomDTO(id, room.getSensors(),room.getName());
    }
}
