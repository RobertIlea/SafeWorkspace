/**
 * SensorController.java
 * This file represents the REST controller for managing sensor-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.EmptyResultException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SensorController handles HTTP requests related to sensor operations.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/sensor" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@RequestMapping("/sensor")
@CrossOrigin(origins = "http://localhost:4200")
public class SensorController {

    /**
     * The SensorService is injected to handle business logic related to sensors.
     */
    @Autowired
    private SensorService sensorService;

    /**
     * This method handles GET requests to retrieve a sensor by its ID.
     * @param sensorId the ID of the sensor to retrieve
     * @return ResponseEntity containing the SensorDTO object
     * @throws ObjectNotFound if the sensor with the specified ID is not found
     */
    @GetMapping("/{sensorId}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable String sensorId) throws ObjectNotFound {
        SensorDTO sensorDTO = sensorService.getSensorById(sensorId);

        if(sensorDTO == null) {
            throw new ObjectNotFound("Sensor with ID " + sensorId + " not found!");
        }

        return new ResponseEntity<>(sensorDTO, HttpStatus.OK);

    }

    /**
     * This method handles GET requests to retrieve all sensors.
     * @return ResponseEntity containing a list of SensorDTO objects
     * @throws ObjectNotFound if the sensor list is empty or not found
     */
    @GetMapping("/")
    public ResponseEntity<List<SensorDTO>> getSensors() throws ObjectNotFound {
        List<SensorDTO> sensors = sensorService.getSensors();

        if(sensors == null || sensors.isEmpty()) {
            throw new ObjectNotFound("Sensor list is empty or not found!");
        }

        return new ResponseEntity<>(sensors, HttpStatus.OK);

    }

    /**
     * This method handles POST requests to add a new sensor.
     * @param sensor the Sensor object to be added
     * @return ResponseEntity containing the created SensorDTO object
     * @throws CreationException if the sensor could not be created
     */
    @PostMapping("/")
    public ResponseEntity<SensorDTO> addSensor(@RequestBody Sensor sensor) throws CreationException {
        SensorDTO sensorDTO = sensorService.addSensor(sensor);

        if(sensorDTO == null) {
            throw new CreationException("Sensor could not be created because it's null!");
        }

        return new ResponseEntity<>(sensorDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles DELETE requests to remove a sensor by its ID.
     * @param id the ID of the sensor to delete
     * @return ResponseEntity containing the deleted SensorDTO object
     * @throws ObjectNotFound if the sensor with the specified ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<SensorDTO> deleteSensor(@PathVariable String id) throws ObjectNotFound {
        SensorDTO sensorDTO = sensorService.deleteSensorById(id);

        if(sensorDTO == null) {
            throw new ObjectNotFound("Sensor with ID " + id + " not found!");
        }

        return new ResponseEntity<>(sensorDTO, HttpStatus.OK);
    }

    /**
     * This method handles PUT requests to update an existing sensor.
     * @param id the ID of the sensor to update
     * @param sensor the Sensor object containing updated information
     * @return ResponseEntity containing the updated SensorDTO object
     * @throws ObjectNotFound if the sensor with the specified ID is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable String id, @RequestBody Sensor sensor) throws ObjectNotFound {
        SensorDTO sensorDTO = sensorService.updateSensor(id,sensor);

        if(sensorDTO == null) {
            throw new ObjectNotFound("Sensor with ID " + id + " not found!");
        }

        return new ResponseEntity<>(sensorDTO, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve sensor data for a specific date.
     * @param sensorId the ID of the sensor for which data is requested
     * @param date the date in "yyyy-MM-dd" format for which data is requested
     * @return ResponseEntity containing a list of Details objects for the specified date
     * @throws ObjectNotFound if the sensor with the specified ID is not found
     */
    @GetMapping("/{sensorId}/data/{date}")
    public ResponseEntity<List<Details>> getSensorDataByDate(@PathVariable String sensorId, @PathVariable String date) throws ObjectNotFound {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return ResponseEntity.badRequest().build();
        }

        SensorDTO sensorDTO = sensorService.getSensorById(sensorId);

        if(sensorDTO == null) {
            throw new ObjectNotFound("Sensor with ID " + sensorId + " not found!");
        }

        LocalDate localDate = LocalDate.parse(date);
        Date selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Details> details = sensorService.getSensorDataByDate(sensorId, selectedDate);

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve the last details for a specific sensor.
     * @param sensorId the ID of the sensor for which the last details are requested
     * @return ResponseEntity containing the last Details object for the specified sensor
     * @throws EmptyResultException if no details are found for the specified sensor
     */
    @GetMapping("/last/details/{sensorId}")
    public ResponseEntity<Details> getLastSensorDetails(@PathVariable String sensorId) throws EmptyResultException {
        Details details = sensorService.getLastDetailForSensor(sensorId);

        if(details == null){
            throw new EmptyResultException("No details yet for sensor: " + sensorId);
        }

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /**
     * This method handles POST requests to set the status of a sensor.
     * @param sensorId the ID of the sensor for which the status is being set
     * @param payload a map containing the status to be set (active or inactive)
     * @return ResponseEntity containing a message indicating the status change
     */
    @PutMapping("/{sensorId}/status")
    public ResponseEntity<Map<String, String>> setSensorStatus(@PathVariable String sensorId, @RequestBody Map<String, Object> payload) {
        boolean status = (Boolean) payload.get("active");

        sensorService.setStatusForSensor(sensorId, status);

        Map<String, String> response = Map.of("message", "Sensor with ID " + sensorId + " has been " + (status ? "activated" : "deactivated") + ".");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
