package org.example.springproject.controller;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sensor")
@CrossOrigin(origins = "http://localhost:4200")

public class SensorController {
    @Autowired
    private SensorService sensorService;

    @GetMapping("/{sensorId}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable String sensorId){
        try{
            SensorDTO sensorDTO = sensorService.getSensorById(sensorId);
            return ResponseEntity.ok(sensorDTO);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new SensorDTO());
        }
    }
    @GetMapping("/")
    public ResponseEntity<List<SensorDTO>> getSensors(){
        try{
            List<SensorDTO> sensors = sensorService.getSensors();
            return ResponseEntity.ok(sensors);
        }catch (RuntimeException e) {
            List<SensorDTO> errorList = new ArrayList<>();
            SensorDTO errorDTO = new SensorDTO();
            errorList.add(errorDTO);
            return ResponseEntity.badRequest().body(errorList);
        } catch (Exception e) {
            List<SensorDTO> errorList = new ArrayList<>();
            SensorDTO errorDTO = new SensorDTO();
            errorList.add(errorDTO);
            return new ResponseEntity<>(errorList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/")
    public ResponseEntity<SensorDTO> addSensor(@RequestBody Sensor sensor){
        try{
            SensorDTO sensorDTO = sensorService.addSensor(sensor);
            return ResponseEntity.ok(sensorDTO);
        }catch (RuntimeException e){
            SensorDTO errorDTO = new SensorDTO();
            return ResponseEntity.badRequest().body(errorDTO);
        }catch (Exception e){
            SensorDTO errorDTO = new SensorDTO();
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SensorDTO> deleteSensor(@PathVariable String id){
        try{
            SensorDTO sensorDTO = sensorService.deleteSensorById(id);
            return ResponseEntity.ok(sensorDTO);
        }catch (RuntimeException e){
            SensorDTO errorDTO = new SensorDTO();
            return ResponseEntity.badRequest().body(errorDTO);
        }catch (Exception e){
            SensorDTO errorDTO = new SensorDTO();
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable String id, @RequestBody Sensor sensor){
        try{
            SensorDTO sensorDTO = sensorService.updateSensor(id,sensor);
            return ResponseEntity.ok(sensorDTO);
        }catch (RuntimeException e){
            SensorDTO errorDTO = new SensorDTO();
            return ResponseEntity.badRequest().body(errorDTO);
        }catch (Exception e){
            SensorDTO errorDTO = new SensorDTO();
            return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/types")
    public ResponseEntity<List<String>> getSensorsTypes(){
        List<String> sensorsTypes = sensorService.getSensorsType();
        return ResponseEntity.ok(sensorsTypes);
    }
}
