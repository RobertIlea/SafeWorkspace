package org.example.springproject.controller;

import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;
import org.example.springproject.service.CustomAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custom-alert")
@CrossOrigin(origins = "http://localhost:4200")

public class CustomAlertController {
    @Autowired
    private CustomAlertService customAlertService;

    @PostMapping("/")
    public ResponseEntity<CustomAlertDTO> createCustomAlert(@RequestBody CustomAlert customAlert) {
        try{
            CustomAlertDTO customAlertDTO = customAlertService.saveCustomAlert(customAlert);
            return ResponseEntity.ok(customAlertDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CustomAlertDTO>> getCustomAlertsByUserId(@PathVariable String userId) {
        try {
            List<CustomAlertDTO> customAlertDTOs = customAlertService.getCustomAlertsByUserId(userId);
            return ResponseEntity.ok(customAlertDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<CustomAlertDTO>> getAllCustomAlerts() {
        try {
            List<CustomAlertDTO> customAlertDTOs = customAlertService.getAllCustomAlerts();
            return ResponseEntity.ok(customAlertDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomAlertDTO> getCustomAlertById(@PathVariable String id) {
        try {
            CustomAlertDTO customAlertDTO = customAlertService.getCustomAlertById(id);
            return ResponseEntity.ok(customAlertDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<CustomAlertDTO> deleteCustomAlertById(@PathVariable String alertId) {
        try {
            CustomAlertDTO customAlertDTO = customAlertService.deleteAlertById(alertId);
            return ResponseEntity.ok(customAlertDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PutMapping("/{alertId}")
    public ResponseEntity<CustomAlertDTO> updateCustomAlert(@PathVariable String alertId, @RequestBody CustomAlert customAlert) {
        try {
            CustomAlertDTO customAlertDTO = customAlertService.updateCustomAlert(alertId, customAlert);
            return ResponseEntity.ok(customAlertDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
