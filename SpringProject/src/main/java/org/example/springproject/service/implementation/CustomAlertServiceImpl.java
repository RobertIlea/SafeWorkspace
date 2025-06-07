/**
 * CustomAlertServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the CustomAlertService interface.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;
import org.example.springproject.service.CustomAlertService;
import org.example.springproject.util.CustomAlertMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomAlertServiceImpl is a service class that implements the CustomAlertService interface.
 * It provides methods to manage custom alerts in the Firestore database.
 */
@Service
public class CustomAlertServiceImpl implements CustomAlertService {

    /**
     * Firestore instance used to interact with the Firestore database.
     */
    private final Firestore firestore;

    /**
     * The name of the collection in Firestore where custom alerts are stored.
     */
    private static final String CUSTOM_ALERTS_COLLECTION = "custom_alerts";

    /**
     * Constructor for CustomAlertServiceImpl.
     * @param firestore The Firestore instance used to interact with the database.
     */
    public CustomAlertServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves a custom alert to the Firestore database.
     * @param customAlert The custom alert to be saved.
     * @return A CustomAlertDTO object containing the details of the saved custom alert.
     * @throws RuntimeException if there is an error while saving the custom alert.
     */
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

    /**
     * Retrieves all custom alerts from the Firestore database.
     * @return A list of CustomAlertDTO objects containing the details of all custom alerts.
     * @throws RuntimeException if there is an error while retrieving the custom alerts.
     */
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
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving custom alerts: " + e.getMessage());
        }
    }

    /**
     * Retrieves all custom alerts for a specific sensor from the Firestore database.
     * @param sensorId The ID of the sensor for which custom alerts are to be retrieved.
     * @return A list of CustomAlert objects containing the details of all custom alerts for the specified sensor.
     * @throws RuntimeException if there is an error while retrieving the custom alerts.
     */
    @Override
    public List<CustomAlert> getAllCustomAlertsBySensorId(String sensorId) throws RuntimeException {
        try {
            return firestore.collection(CUSTOM_ALERTS_COLLECTION)
                    .whereEqualTo("sensorId", sensorId)
                    .get()
                    .get()
                    .toObjects(CustomAlert.class);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving custom alerts by sensor ID: " + e.getMessage());
        }
    }

    /**
     * Retrieves a custom alert by its ID from the Firestore database.
     * @param alertId The ID of the custom alert to be retrieved.
     * @return A CustomAlertDTO object containing the details of the custom alert.
     * @throws RuntimeException if there is an error while retrieving the custom alert.
     */
    @Override
    public CustomAlertDTO getCustomAlertById(String alertId) throws RuntimeException {
        try {
            CustomAlert customAlert = firestore.collection(CUSTOM_ALERTS_COLLECTION).document(alertId).get().get().toObject(CustomAlert.class);
            if (customAlert != null) {
                return CustomAlertMapper.toDTO(alertId, customAlert);
            } else {
                throw new RuntimeException("Custom alert not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving custom alert by ID: " + e.getMessage() + e);
        }
    }

    /**
     * Retrieves all custom alerts for a specific user from the Firestore database.
     * @param userId The ID of the user for whom custom alerts are to be retrieved.
     * @return A list of CustomAlertDTO objects containing the details of all custom alerts for the specified user.
     * @throws RuntimeException if there is an error while retrieving the custom alerts.
     */
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
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving custom alerts by user ID: " + e.getMessage());
        }
    }

    /**
     * Deletes a custom alert by its ID from the Firestore database.
     * @param alertId The ID of the custom alert to be deleted.
     * @return A CustomAlertDTO object containing the details of the deleted custom alert.
     * @throws RuntimeException if there is an error while deleting the custom alert.
     */
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
        } catch (Exception e) {
            throw new RuntimeException("Error deleting custom alert by ID: " + e.getMessage());
        }
    }

    /**
     * Updates a custom alert by its ID in the Firestore database.
     * @param alertId The ID of the custom alert to be updated.
     * @param updatedAlert The updated custom alert object.
     * @return A CustomAlertDTO object containing the details of the updated custom alert.
     * @throws RuntimeException if there is an error while updating the custom alert.
     */
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

        } catch (Exception e) {
            throw new RuntimeException("Error updating custom alert: " + e.getMessage());
        }
    }

    /**
     * Deletes all custom alerts associated with a specific room ID and user ID from the Firestore database.
     * @param roomId the ID of the room for which custom alerts are to be deleted
     * @param userId the ID of the user for whom custom alerts are to be deleted
     * @throws RuntimeException if there is an error while deleting the custom alerts
     */
    @Override
    public void deleteCustomAlertsByRoomIdAndUserId(String roomId, String userId) throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(CUSTOM_ALERTS_COLLECTION)
                    .whereEqualTo("roomId", roomId)
                    .whereEqualTo("userId", userId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for(QueryDocumentSnapshot document : documents) {
                String alertId = document.getId();
                firestore.collection(CUSTOM_ALERTS_COLLECTION).document(alertId).delete().get();
            }
        }catch(Exception e){
            throw new RuntimeException("Error deleting custom alerts by room ID and user ID: " + e.getMessage());
        }
    }

}
