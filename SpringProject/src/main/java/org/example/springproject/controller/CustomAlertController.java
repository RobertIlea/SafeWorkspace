/**
 * CustomAlertController.java
 * This file represents the REST controller for managing custom alert-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.CustomAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CustomAlertController handles HTTP requests related to custom alert operations.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/custom-alert" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@RequestMapping("/custom-alert")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomAlertController {

    /**
     * The CustomAlertService is injected to handle business logic related to custom alerts.
     */
    @Autowired
    private CustomAlertService customAlertService;

    /**
     * This method handles POST requests to create a new custom alert.
     * @param customAlert the CustomAlert object to be created
     * @return ResponseEntity containing the created CustomAlertDTO object
     * @throws CreationException if the creation of the custom alert fails
     */
    @PostMapping("/")
    public ResponseEntity<CustomAlertDTO> createCustomAlert(@RequestBody CustomAlert customAlert) throws CreationException {
        CustomAlertDTO customAlertDTO = customAlertService.saveCustomAlert(customAlert);

        if(customAlertDTO == null) {
            throw new CreationException("Failed to create custom alert because it's null!");
        }

        return new ResponseEntity<>(customAlertDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles GET requests to retrieve custom alerts by user ID.
     * @param userId the ID of the user for whom to retrieve custom alerts
     * @return ResponseEntity containing a list of CustomAlertDTO objects
     * @throws ObjectNotFound if no custom alerts are found for the specified user ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CustomAlertDTO>> getCustomAlertsByUserId(@PathVariable String userId) throws ObjectNotFound {
        List<CustomAlertDTO> customAlertDTOs = customAlertService.getCustomAlertsByUserId(userId);

        if(customAlertDTOs == null || customAlertDTOs.isEmpty()) {
            throw new ObjectNotFound("No custom alerts found for user with ID: " + userId);
        }

        return new ResponseEntity<>(customAlertDTOs, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve all custom alerts.
     * @return ResponseEntity containing a list of CustomAlertDTO objects
     * @throws ObjectNotFound if no custom alerts are found
     */
    @GetMapping("/")
    public ResponseEntity<List<CustomAlertDTO>> getAllCustomAlerts() throws ObjectNotFound {
        List<CustomAlertDTO> customAlertDTOs = customAlertService.getAllCustomAlerts();

        if(customAlertDTOs == null || customAlertDTOs.isEmpty()) {
            throw new ObjectNotFound("No custom alerts found");
        }

        return new ResponseEntity<>(customAlertDTOs, HttpStatus.OK);
    }

    /**
     * This method handles GET requests to retrieve a custom alert by its ID.
     * @param id the ID of the custom alert to retrieve
     * @return ResponseEntity containing the CustomAlertDTO object
     * @throws ObjectNotFound if the custom alert with the specified ID is not found
     */
    @GetMapping("{id}")
    public ResponseEntity<CustomAlertDTO> getCustomAlertById(@PathVariable String id) throws ObjectNotFound {
        CustomAlertDTO customAlertDTO = customAlertService.getCustomAlertById(id);

        if(customAlertDTO == null) {
            throw new ObjectNotFound("Custom alert with ID " + id + " not found");
        }

        return new ResponseEntity<>(customAlertDTO, HttpStatus.OK);
    }

    /**
     * This method handles DELETE requests to remove a custom alert by its ID.
     * @param alertId the ID of the custom alert to delete
     * @return ResponseEntity containing the deleted CustomAlertDTO object
     * @throws ObjectNotFound if the custom alert with the specified ID is not found
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<CustomAlertDTO> deleteCustomAlertById(@PathVariable String alertId) throws ObjectNotFound {
        CustomAlertDTO customAlertDTO = customAlertService.deleteAlertById(alertId);

        if(customAlertDTO == null) {
            throw new ObjectNotFound("Custom alert with ID " + alertId + " not found");
        }

        return new ResponseEntity<>(customAlertDTO, HttpStatus.OK);
    }

    /**
     * This method handles PUT requests to update an existing custom alert.
     * @param alertId the ID of the custom alert to update
     * @param customAlert the CustomAlert object containing the updated information
     * @return ResponseEntity containing the updated CustomAlertDTO object
     * @throws ObjectNotFound if the custom alert with the specified ID is not found
     */
    @PutMapping("/{alertId}")
    public ResponseEntity<CustomAlertDTO> updateCustomAlert(@PathVariable String alertId, @RequestBody CustomAlert customAlert) throws ObjectNotFound {
        CustomAlertDTO customAlertDTO = customAlertService.updateCustomAlert(alertId, customAlert);

        if(customAlertDTO == null) {
            throw new ObjectNotFound("Custom alert with ID " + alertId + " not found");
        }

        return new ResponseEntity<>(customAlertDTO, HttpStatus.OK);
    }
}
