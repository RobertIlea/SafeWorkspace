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

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private Firestore firestore;
    private static final String ALERT_COLLECTION = "alerts";


    @Override
    public AlertDTO saveAlert(Alert alert){
        try{
            DocumentReference alertRef = firestore.collection(ALERT_COLLECTION).document();
            alertRef.set(alert).get();
            AlertDTO alertDTO = new AlertDTO(alertRef.getId(),alert.getRoomId(),alert.getSensorId(),alert.getTimestamp(),alert.getSensorType(), alert.getData(),alert.getMessage());
            return alertDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<AlertDTO> getAlerts(String roomId) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(ALERT_COLLECTION)
                    .whereEqualTo("roomId", roomId)
                    .get();
            return getAlertDTOS(future);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AlertDTO> getAlertsByRoomAndDate(String roomId, Date selectedDate) {
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
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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
