package org.example.springproject.util;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;

public class UserMapper {
    public static User toEntity(UserDTO userDTO){
        if(userDTO == null){
            return null;
        }
        return new User(userDTO.getName(),userDTO.getEmail(),userDTO.getPassword());
    }
    public static UserDTO toDTO(User user,String id){
        if(user == null){
            return null;
        }
        return new UserDTO(id, user.getName(), user.getEmail(), user.getPassword());
    }
}
