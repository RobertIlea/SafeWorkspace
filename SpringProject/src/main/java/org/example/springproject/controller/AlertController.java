/**
 * AlertController.java
 * This file represents the REST controller for managing alert-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * AlertController handles HTTP requests related to alert operations.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/alerts" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertController {

    /**
     * The AlertService is injected to handle business logic related to alerts.
     */
    @Autowired
    private AlertService alertService;

    /**
     * This method handles POST requests to create a new alert.
     * @param alert the Alert object to be created
     * @return ResponseEntity containing the created AlertDTO object
     * @throws CreationException if the creation of the alert fails
     */
    @PostMapping("/")
    public ResponseEntity<AlertDTO> addAlert(@RequestBody Alert alert) throws CreationException {
        AlertDTO alertDTO = alertService.saveAlert(alert);

        if(alertDTO == null) {
            throw new CreationException("Failed to create alert because it's null!");
        }

        return new ResponseEntity<>(alertDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles GET requests to retrieve alerts by room ID.
     * @param roomId the ID of the room for which to retrieve alerts
     * @return ResponseEntity containing a list of AlertDTO objects
     * @throws ObjectNotFound if no alerts are found for the specified room ID
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<AlertDTO>> getAlerts(@PathVariable("roomId") String roomId) throws ObjectNotFound {
        List<AlertDTO> alertDTOs = alertService.getAlerts(roomId);

        if(alertDTOs == null || alertDTOs.isEmpty()) {
            throw new ObjectNotFound("No alerts found for room with ID: " + roomId);
        }

        return new ResponseEntity<>(alertDTOs, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve alerts by room ID and date.
     * @param roomId the ID of the room for which to retrieve alerts
     * @param date the date in the format YYYY-MM-DD for which to retrieve alerts
     * @return ResponseEntity containing a list of AlertDTO objects
     * @throws ObjectNotFound if no alerts are found for the specified room ID and date
     */
    @GetMapping("/{roomId}/data/{date}")
    public ResponseEntity<List<AlertDTO>> getAlertsByRoomAndDate(@PathVariable("roomId") String roomId, @PathVariable("date") String date) throws ObjectNotFound {
        // Validate the date format (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ObjectNotFound("Invalid date format. Expected format: YYYY-MM-DD");
        }

        LocalDate localDate = LocalDate.parse(date);
        Date selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<AlertDTO> alertDTOList = alertService.getAlertsByRoomAndDate(roomId,selectedDate);

        if(alertDTOList == null || alertDTOList.isEmpty()) {
            throw new ObjectNotFound("No alerts found for room with ID: " + roomId + " on date: " + date);
        }

        return new ResponseEntity<>(alertDTOList, HttpStatus.OK);
    }
}
