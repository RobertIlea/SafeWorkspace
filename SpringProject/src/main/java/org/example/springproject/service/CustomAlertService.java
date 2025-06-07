/**
 * CustomAlertService.java
 * This interface defines the contract for managing custom alerts in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;

import java.util.List;

/**
 * CustomAlertService provides methods to manage custom alerts, including saving, retrieving, updating, and deleting alerts.
 */
public interface CustomAlertService {

    /**
     * Saves a new custom alert.
     * @param customAlert the custom alert to be saved
     * @return the saved CustomAlertDTO object
     */
    CustomAlertDTO saveCustomAlert(CustomAlert customAlert);

    /**
     * Retrieves all custom alerts.
     * @return a list of CustomAlertDTO objects representing all custom alerts
     */
    List<CustomAlertDTO> getAllCustomAlerts();

    /**
     * Retrieves all custom alerts associated with a specific sensor ID.
     * @param sensorId the ID of the sensor for which to retrieve custom alerts
     * @return a list of CustomAlert objects associated with the specified sensor ID
     */
    List<CustomAlert> getAllCustomAlertsBySensorId(String sensorId);

    /**
     * Retrieves a custom alert by its ID.
     * @param id the ID of the custom alert to retrieve
     * @return the CustomAlertDTO object associated with the specified ID
     */
    CustomAlertDTO getCustomAlertById(String id);

    /**
     * Retrieves all custom alerts associated with a specific user ID.
     * @param userId the ID of the user for whom to retrieve custom alerts
     * @return a list of CustomAlertDTO objects associated with the specified user ID
     */
    List<CustomAlertDTO> getCustomAlertsByUserId(String userId);

    /**
     * Deletes a custom alert by its ID.
     * @param alertId the ID of the custom alert to delete
     * @return the deleted CustomAlertDTO object
     */
    CustomAlertDTO deleteAlertById(String alertId);

    /**
     * Updates an existing custom alert.
     * @param alertId the ID of the custom alert to update
     * @param updatedAlert the updated CustomAlert object
     * @return the updated CustomAlertDTO object
     */
    CustomAlertDTO updateCustomAlert(String alertId, CustomAlert updatedAlert);

    void deleteCustomAlertsByRoomIdAndUserId(String roomId, String userId) throws RuntimeException;
}
