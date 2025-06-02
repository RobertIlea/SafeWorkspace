/**
 * AlertServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the AlertService interface.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;
import org.example.springproject.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * AlertServiceImpl is a service class that implements the AlertService interface.
 */
@Service
public class AlertServiceImpl implements AlertService {

    /**
     * Firestore instance used to interact with the Firestore database.
     */
    @Autowired
    private Firestore firestore;

    /**
     * The name of the collection in Firestore where alerts are stored.
     */
    private static final String ALERT_COLLECTION = "alerts";

    /**
     * Saves an alert to the Firestore database.
     * @param alert The alert to be saved.
     * @return An AlertDTO object containing the details of the saved alert.
     * @throws RuntimeException if there is an error while saving the alert.
     */
    @Override
    public AlertDTO saveAlert(Alert alert) throws RuntimeException {
        try{
            DocumentReference alertRef = firestore.collection(ALERT_COLLECTION).document();
            alertRef.set(alert).get();
            return new AlertDTO(alertRef.getId(),alert.getRoomId(),alert.getSensorId(),alert.getTimestamp(),alert.getSensorType(), alert.getData(),alert.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error saving alert: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all alerts from the Firestore database.
     * @return A list of AlertDTO objects containing the details of all alerts.
     * @throws RuntimeException if there is an error while retrieving the alerts.
     */
    @Override
    public List<AlertDTO> getAlerts(String roomId) throws RuntimeException {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(ALERT_COLLECTION)
                    .whereEqualTo("roomId", roomId)
                    .get();
            return getAlertDTOS(future);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving alerts: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves alerts for a specific room and date from the Firestore database.
     * @param roomId The ID of the room for which alerts are to be retrieved.
     * @param selectedDate The date for which alerts are to be retrieved.
     * @return A list of AlertDTO objects containing the details of the alerts for the specified room and date.
     * @throws RuntimeException if there is an error while retrieving the alerts.
     */
    @Override
    public List<AlertDTO> getAlertsByRoomAndDate(String roomId, Date selectedDate) throws RuntimeException {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endOfDay = calendar.getTime();

            ApiFuture<QuerySnapshot> future = firestore.collection(ALERT_COLLECTION).whereEqualTo("roomId", roomId).whereGreaterThanOrEqualTo("timestamp",startOfDay).whereLessThanOrEqualTo("timestamp",endOfDay).get();
            return getAlertDTOS(future);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving alerts by room and date: " + e.getMessage(), e);
        }
    }

    /**
     * Get AlertDTOs from the ApiFuture<QuerySnapshot>.
     * This method processes the QuerySnapshot returned by Firestore and converts it into a list of AlertDTO objects.
     * @param future The ApiFuture<QuerySnapshot> containing the results of the Firestore query.
     * @return A list of AlertDTO objects.
     * @throws InterruptedException if the thread is interrupted while waiting for the future to complete.
     * @throws ExecutionException if there is an error during the execution of the future.
     */
    private List<AlertDTO> getAlertDTOS(ApiFuture<QuerySnapshot> future) throws InterruptedException, ExecutionException {
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<AlertDTO> alertDTOList = new ArrayList<>();


        for (QueryDocumentSnapshot document : documents) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) document.get("data");
            Map<String, Float> sensorData = new HashMap<>();
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Number) {
                        sensorData.put(entry.getKey(), ((Number) value).floatValue());
                    } else {
                        System.out.println("Warning: Unexpected value type for key " + entry.getKey() + ": " + value);
                    }
                }
            }
            AlertDTO alertDTO = new AlertDTO(document.getId(), document.getString("roomId"), document.getString("sensorId"), document.getTimestamp("timestamp"), document.getString("sensorType"), sensorData, document.getString("message"));
            alertDTOList.add(alertDTO);

        }
        return alertDTOList;
    }
}
