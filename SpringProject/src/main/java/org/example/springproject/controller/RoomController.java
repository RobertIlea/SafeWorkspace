package org.example.springproject.controller;

import com.google.api.Authentication;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
//import org.example.springproject.service.JwtService;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.RoomService;
import org.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/")
    public ResponseEntity<List<RoomDTO>> getRooms(@RequestHeader("Authorization") String token) {
        try{
            String jwtToken = token.substring(7);
            String email = jwtService.extractEmail(jwtToken);

            List<RoomDTO> rooms = roomService.getRoomsByUserEmail(email);
            return ResponseEntity.ok(rooms);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("{roomId}/sensors")
    public ResponseEntity<List<SensorDTO>> getSensorsByRoomId(@PathVariable String roomId){
        try{
            List<SensorDTO> sensorDTOS = roomService.getSensorsByRoomId(roomId);
            return ResponseEntity.ok(sensorDTOS);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(){
        try{
            List<RoomDTO> rooms = roomService.getAvailableRooms();
            return ResponseEntity.ok(rooms);
        }catch (RuntimeException e) {
            List<RoomDTO> errorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(), "","");
            errorList.add(errorDTO);
            return ResponseEntity.badRequest().body(errorList);
        } catch (Exception e) {
            List<RoomDTO> errorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(),"","");
            errorList.add(errorDTO);
            return new ResponseEntity<>(errorList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<RoomDTO>> getRoomsByUserId(@PathVariable String id){
        try{
            List<RoomDTO> rooms = roomService.getRoomsByUserId(id);
            return ResponseEntity.ok(rooms);
        }catch (RuntimeException e) {
            List<RoomDTO> errorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(), "","");
            errorList.add(errorDTO);
            return ResponseEntity.badRequest().body(errorList);
        } catch (Exception e) {
            List<RoomDTO> errorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(),"","");
            errorList.add(errorDTO);
            return new ResponseEntity<>(errorList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable String roomId){
        try{
            RoomDTO roomDTO = roomService.getRoomById(roomId);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/")
    public ResponseEntity<RoomDTO> addRoom(@RequestBody Room room)  {
        try{
            RoomDTO roomDTO = roomService.addRoom(room);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(),"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e) {
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(),"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoomDTO> deleteRoomById(@PathVariable String id){
        try{
            RoomDTO roomDTO = roomService.deleteRoomById(id);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{roomId}/remove/{userId}")
    public ResponseEntity<RoomDTO> removeRoomById(@PathVariable String roomId, @PathVariable String userId){
        try{
            RoomDTO roomDTO = roomService.removeUserFromRoom(roomId, userId);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable String id, @RequestBody Room room){
        try{
            RoomDTO roomDTO = roomService.updateRoom(id,room);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/test/{id}")
    public ResponseEntity<RoomDTO> addSensorToRoom(@PathVariable String id, @RequestBody Map<String, String> requestBody){
        try{
            String sensorId = requestBody.get("sensorId");
            if (sensorId == null) {
                throw new RuntimeException("Invalid request: sensorId is missing");
            }
            RoomDTO roomDTO = roomService.addSensorToRoom(id,sensorId);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<RoomDTO> assignRoomToUser(@RequestBody Map<String, Object> requestBody){
        try{
            String roomId = (String) requestBody.get("roomId");
            String userId = (String) requestBody.get("userId");
            String roomName = (String) requestBody.get("roomName");
            List<String> sensorIds = ((List<?>) requestBody.get("sensorIds"))
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            System.out.println("Sensor ids after parsing: " + sensorIds);
            if (roomId == null || userId == null) {
                throw new RuntimeException("Invalid request: roomId or userId is missing");
            }
            RoomDTO roomDTO = roomService.assignRoomToUser(roomId,userId,roomName,sensorIds);
            return ResponseEntity.ok(roomDTO);
        }catch (RuntimeException e) {
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return ResponseEntity.badRequest().body(errorDTO);
        } catch (Exception e){
            List<SensorDTO> sensorList = new ArrayList<>();
            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), sensorList,"","");
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
