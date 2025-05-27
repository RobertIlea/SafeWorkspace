package org.example.springproject.service;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;

import java.util.List;

public interface UserService {
    UserDTO addUser(User user) throws RuntimeException;

    UserDTO deleteUserbyId(String id) throws RuntimeException;

    UserDTO updateUser(String id, User updatedUser);

    List<UserDTO> getUsers();

    UserDTO getUserById(String id);

    User getUserByEmail(String email);

    String getUserIdByEmail(String email);

    UserDTO getUserByRoomId(String roomId);
}
