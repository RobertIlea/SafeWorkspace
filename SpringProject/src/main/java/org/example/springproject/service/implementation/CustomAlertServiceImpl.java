package org.example.springproject.service.implementation;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;
import org.example.springproject.service.CustomAlertService;
import org.example.springproject.util.CustomAlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CustomAlertServiceImpl implements CustomAlertService {
    @Autowired
    private Firestore firestore;
    private static final String CUSTOM_ALERTS_COLLECTION = "custom_alerts";

    @Override
    public CustomAlertDTO saveCustomAlert(CustomAlert customAlert) throws RuntimeException {
        try{
            if(customAlert.getUserId() == null || customAlert.getRoomId() == null || customAlert.getSensorId() == null) {
                throw new IllegalArgumentException("User ID, Room ID, and Sensor ID cannot be null");
            }
            DocumentReference customAlertRef = firestore.collection(CUSTOM_ALERTS_COLLECTION).document();
            customAlertRef.set(customAlert).get();
            return CustomAlertMapper.toDTO(customAlertRef.getId(),customAlert);
        }catch (Exception e){
            throw new RuntimeException("Error saving custom alert: " + e.getMessage());
        }
    }

    @Override
    public List<CustomAlertDTO> getAllCustomAlerts() throws RuntimeException {
        try{
            return firestore.collection(CUSTOM_ALERTS_COLLECTION)
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(doc -> CustomAlertMapper.toDTO(doc.getId(), doc.toObject(CustomAlert.class)))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CustomAlert> getAllCustomAlertsBySensorId(String sensorId) throws RuntimeException {
        try {
            return firestore.collection(CUSTOM_ALERTS_COLLECTION)
                    .whereEqualTo("sensorId", sensorId)
                    .get()
                    .get()
                    .toObjects(CustomAlert.class);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomAlertDTO getCustomAlertById(String alertId) throws RuntimeException {
        try {
            CustomAlert customAlert = firestore.collection(CUSTOM_ALERTS_COLLECTION).document(alertId).get().get().toObject(CustomAlert.class);
            if (customAlert != null) {
                return CustomAlertMapper.toDTO(alertId, customAlert);
            } else {
                throw new RuntimeException("Custom alert not found");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CustomAlertDTO> getCustomAlertsByUserId(String userId) throws RuntimeException {
        try {
            return firestore.collection(CUSTOM_ALERTS_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(doc -> CustomAlertMapper.toDTO(doc.getId(), doc.toObject(CustomAlert.class)))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomAlertDTO deleteAlertById(String alertId) throws RuntimeException {
        try {
            DocumentReference documentReference = firestore.collection(CUSTOM_ALERTS_COLLECTION).document(alertId);
            DocumentSnapshot documentSnapshot = documentReference.get().get();

            if (!documentSnapshot.exists()) {
                throw new RuntimeException("Custom alert not found");
            }

            CustomAlert customAlert = documentSnapshot.toObject(CustomAlert.class);
            if (customAlert == null) {
                throw new RuntimeException("Custom alert not found");
            }

            documentReference.delete().get();
            return CustomAlertMapper.toDTO(alertId,customAlert);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomAlertDTO updateCustomAlert(String alertId, CustomAlert updatedAlert) throws RuntimeException {
        try {
            DocumentReference documentReference = firestore.collection(CUSTOM_ALERTS_COLLECTION).document(alertId);
            DocumentSnapshot documentSnapshot = documentReference.get().get();

            if (!documentSnapshot.exists()) {
                throw new RuntimeException("Custom alert not found");
            }

            CustomAlert currentAlert = documentSnapshot.toObject(CustomAlert.class);
            if (currentAlert == null) {
                throw new RuntimeException("Custom alert not found");
            }

            currentAlert.setRoomId(updatedAlert.getRoomId());
            currentAlert.setSensorId(updatedAlert.getSensorId());
            currentAlert.setSensorType(updatedAlert.getSensorType());
            currentAlert.setMessage(updatedAlert.getMessage());
            currentAlert.setUserId(updatedAlert.getUserId());
            currentAlert.setParameter(updatedAlert.getParameter());
            currentAlert.setCondition(updatedAlert.getCondition());
            currentAlert.setThreshold(updatedAlert.getThreshold());

            documentReference.set(currentAlert).get();
            return CustomAlertMapper.toDTO(alertId,currentAlert);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
