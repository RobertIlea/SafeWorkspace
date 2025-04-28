package org.example.springproject.controller;

import com.google.api.Authentication;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
//import org.example.springproject.service.JwtService;
import org.example.springproject.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

//    @Autowired
//    private JwtService jwtService;

//    @GetMapping("/")
//    public ResponseEntity<List<RoomDTO>> getRooms(@RequestHeader("Authorization") String token) {
//        try{
//            String jwtToken = token.substring(7);
//            String userId = jwtService.extractId(jwtToken);
//
//            List<RoomDTO> rooms = roomService.getRoomsByUserId(userId);
//            return ResponseEntity.ok(rooms);
//        }catch (RuntimeException e) {
//            List<RoomDTO> errorList = new ArrayList<>();
//            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(), "");
//            errorList.add(errorDTO);
//            return ResponseEntity.badRequest().body(errorList);
//        } catch (Exception e) {
//            List<RoomDTO> errorList = new ArrayList<>();
//            RoomDTO errorDTO = new RoomDTO("Error: " + e.getMessage(), new ArrayList<>(), "");
//            errorList.add(errorDTO);
//            return new ResponseEntity<>(errorList, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @GetMapping("{roomId}/sensors")
    public ResponseEntity<List<SensorDTO>> getSensorsByRoomId(@PathVariable String roomId){
        try{
            List<SensorDTO> sensorDTOS = roomService.getSensorsByRoomId(roomId);
            return ResponseEntity.ok(sensorDTOS);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
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

}
