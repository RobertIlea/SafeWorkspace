package org.example.springproject.controller;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsers(){
        try{
            List<UserDTO> users = userService.getUsers();
            return ResponseEntity.ok(users);
        }catch (RuntimeException e) {
            List<UserDTO> errorList = new ArrayList<>();
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            errorList.add(errorDTO);
            return ResponseEntity.badRequest().body(errorList);
        } catch (Exception e) {
            List<UserDTO> errorList = new ArrayList<>();
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            errorList.add(errorDTO);
            return new ResponseEntity<>(errorList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/")
    public ResponseEntity<UserDTO> addUser(@RequestBody User user) {
        try{
            UserDTO userDTO = userService.addUser(user);
            return ResponseEntity.ok(userDTO);
        }catch (RuntimeException e){
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return ResponseEntity.badRequest().body(errorDTO);
        }catch (Exception e){
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable String id) {
        try {
            UserDTO userDTO = userService.deleteUserbyId(id);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody User user){
        try{
            UserDTO userDTO = userService.updateUser(id,user);
            return ResponseEntity.ok(userDTO);
        }catch (RuntimeException e) {
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            UserDTO errorDTO = new UserDTO("Error: " + e.getMessage(), "", "", "");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
