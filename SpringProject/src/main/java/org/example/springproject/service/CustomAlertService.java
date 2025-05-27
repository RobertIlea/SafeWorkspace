package org.example.springproject.service;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;

import java.util.List;

public interface CustomAlertService {
    CustomAlertDTO saveCustomAlert(CustomAlert customAlert);

    List<CustomAlertDTO> getAllCustomAlerts();

    List<CustomAlert> getAllCustomAlertsBySensorId(String sensorId);

    CustomAlertDTO getCustomAlertById(String id);

    List<CustomAlertDTO> getCustomAlertsByUserId(String userId);

    CustomAlertDTO deleteAlertById(String alertId);

    CustomAlertDTO updateCustomAlert(String alertId, CustomAlert updatedAlert);
}
