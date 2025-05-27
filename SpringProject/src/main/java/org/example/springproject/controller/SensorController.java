package org.example.springproject.controller;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

    @GetMapping("/{sensorId}/data/{date}")
    public ResponseEntity<List<Details>> getSensorDataByDate(@PathVariable String sensorId, @PathVariable String date) {
        try {
            System.out.println("Received date: " + date);
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return ResponseEntity.badRequest().build();
            }
            SensorDTO sensorDTO = sensorService.getSensorById(sensorId);
            if(sensorDTO == null) {
                System.out.println("Sensor not found");
                return ResponseEntity.notFound().build();
            }
            LocalDate localDate = LocalDate.parse(date);
            Date selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            System.out.println("Parsed date: " + selectedDate);

            List<Details> details = sensorService.getSensorDataByDate(sensorId, selectedDate);
            System.out.println(details);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/last/details/{sensorId}")
    public ResponseEntity<Details> getLastSensorDetails(@PathVariable String sensorId){
        try{
            System.out.println("Received date: " + sensorId);
            Details details = sensorService.getLastDetailForSensor(sensorId);
            if(details == null){
                System.out.println("details not found");
                return ResponseEntity.notFound().build();
            }
            System.out.println(details);
            return ResponseEntity.ok(sensorService.getLastDetailForSensor(sensorId));
        }catch (RuntimeException e){
            System.out.println("Error while fetching last details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
