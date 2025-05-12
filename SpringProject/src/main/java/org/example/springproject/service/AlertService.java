package org.example.springproject.service;

import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;

import java.util.Date;
import java.util.List;

public interface AlertService {
    AlertDTO saveAlert(Alert alert);

    List<AlertDTO> getAlerts(String roomId);

    List<AlertDTO> getAlertsByRoomAndDate(String roomId, Date selectedDate);
}
