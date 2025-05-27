package org.example.springproject.util;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;

public class CustomAlertMapper {
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
