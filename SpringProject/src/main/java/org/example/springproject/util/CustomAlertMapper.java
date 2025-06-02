/**
 * CustomAlertMapper.java
 * This class is responsible for mapping between CustomAlertDTO and CustomAlert entity.
 * It provides methods to convert a DTO to an entity and vice versa.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;

/**
 * CustomAlertMapper is a utility class that provides methods to convert between CustomAlertDTO and CustomAlert.
 * It is used to facilitate the transfer of data between the service layer and the controller layer.
 */
public class CustomAlertMapper {

    /**
     * Converts a CustomAlertDTO to a CustomAlert entity.
     * @param dto the CustomAlertDTO to convert
     * @return a CustomAlert entity
     */
    public static CustomAlert fromDTO(CustomAlertDTO dto) {
        return new CustomAlert(
                dto.getUserId(),
                dto.getRoomId(),
                dto.getSensorId(),
                dto.getSensorType(),
                dto.getParameter(),
                dto.getCondition(),
                dto.getThreshold(),
                dto.getMessage()
        );
    }

    /**
     * Converts a CustomAlert entity to a CustomAlertDTO.
     * @param id the ID of the CustomAlert
     * @param alert the CustomAlert entity to convert
     * @return a CustomAlertDTO
     */
    public static CustomAlertDTO toDTO(String id,CustomAlert alert) {
        return new CustomAlertDTO(
                id,
                alert.getUserId(),
                alert.getRoomId(),
                alert.getSensorId(),
                alert.getSensorType(),
                alert.getParameter(),
                alert.getCondition(),
                alert.getMessage(),
                alert.getThreshold()
        );
    }
}
