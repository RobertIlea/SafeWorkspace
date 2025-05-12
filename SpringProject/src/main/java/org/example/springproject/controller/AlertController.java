package org.example.springproject.controller;

import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;
import org.example.springproject.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @PostMapping("/")
    public ResponseEntity<AlertDTO> addAlert(@RequestBody Alert alert) {
        try {
            AlertDTO alertDTO = alertService.saveAlert(alert);
            return new ResponseEntity<>(alertDTO, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{roomId}")
    public ResponseEntity<List<AlertDTO>> getAlerts(@PathVariable("roomId") String roomId) {
        try {
            List<AlertDTO> alertDTOs = alertService.getAlerts(roomId);
            if (alertDTOs != null) {
                return new ResponseEntity<>(alertDTOs, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{roomId}/data/{date}")
    public ResponseEntity<List<AlertDTO>> getAlertsByRoomAndDate(@PathVariable("roomId") String roomId, @PathVariable("date") String date){
        try{
            System.out.println("Received date in getAlertsByRoomAndDate: " + date);
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return ResponseEntity.badRequest().build();
            }
            LocalDate localDate = LocalDate.parse(date);
            Date selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<AlertDTO> alertDTOList = alertService.getAlertsByRoomAndDate(roomId,selectedDate);
            return ResponseEntity.ok(alertDTOList);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
