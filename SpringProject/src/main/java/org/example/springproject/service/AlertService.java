/**
 * AlertService.java
 * This interface defines the contract for alert-related operations in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;

import java.util.Date;
import java.util.List;

/**
 * AlertService provides methods to manage alerts, including saving new alerts and retrieving existing ones.
 */
public interface AlertService {

    /**
     * Saves a new alert.
     * @param alert the alert to be saved
     * @return the saved AlertDTO object
     */
    AlertDTO saveAlert(Alert alert);

    /**
     * Retrieves all alerts for a specific room.
     * @param roomId the ID of the room for which to retrieve alerts
     * @return a list of AlertDTO objects associated with the specified room
     */
    List<AlertDTO> getAlerts(String roomId);

    /**
     * Retrieves all alerts for a specific room on a given date.
     * @param roomId the ID of the room for which to retrieve alerts
     * @param selectedDate the date for which to retrieve alerts
     * @return a list of AlertDTO objects associated with the specified room and date
     */
    List<AlertDTO> getAlertsByRoomAndDate(String roomId, Date selectedDate);

    void removeAllAlertsBySensorId(String sensorId) throws RuntimeException;
}
