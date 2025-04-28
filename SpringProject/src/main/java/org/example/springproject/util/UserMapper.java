package org.example.springproject.util;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;

public class UserMapper {
    public static User toEntity(UserDTO userDTO){
        if(userDTO == null){
            return null;
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }
    public static UserDTO toDTO(User user,String id){
        if(user == null){
            return null;
        }
        return new UserDTO(id, user.getName(), user.getEmail());
    }
}
