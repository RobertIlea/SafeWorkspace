/**
 * UserMapper.java
 * This class is responsible for mapping between UserDTO and User entity.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;

/**
 * The UserMapper class provides methods to convert between UserDTO and User entity.
 * It is used to facilitate the transfer of data between the service layer and the controller layer.
 */
public class UserMapper {

    /**
     * Converts a UserDTO to a User entity.
     * @param userDTO the UserDTO to convert
     * @return a User entity or null if userDTO is null
     */
    public static User toEntity(UserDTO userDTO){
        if(userDTO == null){
            return null;
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    /**
     * Converts a User entity to a UserDTO.
     * @param user the User entity to convert
     * @param id the ID of the User
     * @return a UserDTO or null if user is null
     */
    public static UserDTO toDTO(User user,String id){
        if(user == null){
            return null;
        }
        return new UserDTO(id, user.getName(), user.getEmail());
    }
}
