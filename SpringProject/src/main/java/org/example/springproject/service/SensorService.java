/**
 * SensorService.java
 * This interface defines the contract for sensor-related operations in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;

import java.util.Date;
import java.util.List;

/**
 * SensorService provides methods to manage sensors, including adding, deleting, updating and retrieving sensor information.
 */
public interface SensorService {

    /**
     * Adds a new sensor to the system.
     * @param sensor The sensor to be added.
     * @return The added sensor as a SensorDTO.
     */
    SensorDTO addSensor(Sensor sensor);

    /**
     * Deletes a sensor by its ID.
     * @param id The ID of the sensor to be deleted.
     * @return The deleted sensor as a SensorDTO.
     */
    SensorDTO deleteSensorById(String id);

    /**
     * Updates an existing sensor.
     * @param id The ID of the sensor to be updated.
     * @param updatedSensor The updated sensor information.
     * @return The updated sensor as a SensorDTO.
     */
    SensorDTO updateSensor(String id, Sensor updatedSensor);

    /**
     * Retrieves all sensors.
     * @return A list of all sensors as SensorDTOs.
     */
    List<SensorDTO> getSensors();

    /**
     * Saves sensor data.
     * @param sensorDTO The sensor data to be saved.
     * @return A string indicating the result of the save operation.
     */
    String saveSensorData(SensorDTO sensorDTO);

    /**
     * Retrieves a sensor by its ID.
     * @param sensorId The ID of the sensor to be retrieved.
     * @return The sensor as a SensorDTO.
     */
    SensorDTO getSensorById(String sensorId);

    /**
     * Retrieves all sensor data for a specific sensor ID.
     * @param sensorId The ID of the sensor for which to retrieve data.
     * @return A list of Details objects representing the sensor data.
     */
    List<Details> getSensorDataByDate(String sensorId, Date selectedDate);

    /**
     * Retrieves the last detail for a specific sensor ID.
     * @param sensorId The ID of the sensor for which to retrieve the last detail.
     * @return The last Details object for the specified sensor ID.
     */
    Details getLastDetailForSensor(String sensorId);

    /**
     * Clears all sensor details for a specific sensor ID.
     * @param sensorId The ID of the sensor for which to clear details.
     * @throws RuntimeException if an error occurs during the operation.
     */
    void clearAllSensorsDetailsBySensorId(String sensorId) throws RuntimeException;

    /**
     * Clears sensor details from a specific room.
     * @param roomId The ID of the room from which to clear sensor details.
     * @throws RuntimeException if an error occurs during the operation.
     */
    void clearSensorDetailsFromRoom(String roomId) throws RuntimeException;

    /**
     * Activates selected sensors.
     * @param sensorIds A list of sensor IDs to be activated.
     * @throws RuntimeException if an error occurs during the operation.
     */
    void activateSelectedSensor(List<String> sensorIds) throws RuntimeException;

    /**
     * Deactivates selected sensors.
     * @param sensorIds A list of sensor IDs to be deactivated.
     * @throws RuntimeException if an error occurs during the operation.
     */
    void deactivateSelectedSensor(List<String> sensorIds) throws RuntimeException;

    /**
     * Sets the status of a specific sensor.
     * @param sensorId The ID of the sensor whose status is to be set.
     * @param status The new status for the sensor (true for active, false for inactive).
     * @throws RuntimeException if an error occurs during the operation.
     */
    void setStatusForSensor(String sensorId, boolean status) throws RuntimeException;
}
