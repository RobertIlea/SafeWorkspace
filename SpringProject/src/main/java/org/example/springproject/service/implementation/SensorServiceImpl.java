package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.SensorService;
import org.example.springproject.util.SensorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SensorServiceImpl implements SensorService {

    @Autowired
    private Firestore firestore;
    private static final String SENSOR_COLLECTION = "sensors";


    @Override
    public SensorDTO addSensor(Sensor sensor){
        try{
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document();
            sensorRef.set(sensor).get();
            return new SensorDTO(sensorRef.getId(), sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding a new sensor: " + e.getMessage(), e);
        }
    }

    @Override
    public SensorDTO deleteSensorById(String id) {
        try {
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(id);
            DocumentSnapshot sensorSnapshot = sensorRef.get().get();

            if (!sensorSnapshot.exists()) {
                throw new RuntimeException("Sensor with id: " + id + " doesn't exist!");
            }

            Sensor sensor = deserializeSensor(sensorSnapshot);
            sensorRef.delete().get();
            return new SensorDTO(id, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the sensor: " + e.getMessage(), e);
        }
    }

    public Sensor deserializeSensor(DocumentSnapshot sensorSnapshot) throws InterruptedException, ExecutionException {
        String sensorType = sensorSnapshot.getString("sensorType");
        Long portLong = sensorSnapshot.getLong("port");
        Integer port = (portLong != null) ? portLong.intValue() : 0;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> detailsList = (List<Map<String, Object>>) sensorSnapshot.get("details");
        List<Details> details = new ArrayList<>();
        checkDetailsList(detailsList,details);
        return new Sensor(sensorType, port, details);
    }

    @Override
    public SensorDTO updateSensor(String id, Sensor updatedSensor){
        try{
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(id);
            DocumentSnapshot sensorSnapshot = sensorRef.get().get();

            if(!sensorSnapshot.exists()){
                throw new RuntimeException("Sensor with id: " + id + "doesn't exist!");
            }

            sensorRef.set(updatedSensor).get();
            return new SensorDTO(id,updatedSensor.getSensorType(),updatedSensor.getPort(),updatedSensor.getDetails());
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the sensor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SensorDTO> getSensors(){
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(SENSOR_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<SensorDTO> sensors = new ArrayList<>();
            for(QueryDocumentSnapshot document:documents){
                String id = document.getId();
                Sensor sensor = deserializeSensor(document);
                sensors.add(new SensorDTO(id, sensor.getSensorType(), sensor.getPort(), sensor.getDetails()));
            }
            return sensors;
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the sensors: " + e.getMessage(), e);
        }
    }

    @Override
    public String saveSensorData(SensorDTO newSensorData) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(SENSOR_COLLECTION).document(newSensorData.getId());

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot documentSnapshot= future.get();

        List<Details> detailsList = new ArrayList<>();

        if(documentSnapshot.exists()){
            SensorDTO existingSensor = documentSnapshot.toObject(SensorDTO.class);
            if (existingSensor != null && existingSensor.getDetails() != null) {
                detailsList = existingSensor.getDetails();
            }
        }

        detailsList.add(newSensorData.getDetails().get(0));
        SensorDTO updatedSensor = new SensorDTO(
                newSensorData.getId(),
                newSensorData.getSensorType(),
                newSensorData.getPort(),
                detailsList
        );

        WriteResult result = docRef.set(updatedSensor).get();
//        sensorDataWebSocketHandler.broadcast(updatedSensor);
        return result.getUpdateTime().toString();
    }

    @Override
    public List<String> getSensorsType(){
        return Arrays.stream(SensorType.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public SensorDTO getSensorById(String sensorId) {
        try {
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(sensorId);
            ApiFuture<DocumentSnapshot> future = sensorRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                throw new RuntimeException("Sensor with id: " + sensorId + "doesn't exist!");
            }

            Sensor sensor = deserializeSensor(document);
            if (sensor == null) {
                throw new RuntimeException("Deserialization failed: sensor is null");
            }
            return new SensorDTO(sensorId, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkDetailsList(List<Map<String, Object>> detailsList, List<Details> details) {
        if (detailsList != null) {
            try {
                for (Map<String, Object> detailMap : detailsList) {
                    Timestamp timestampObj = (Timestamp) detailMap.get("timestamp");
                    Map<String, Float> data = deserializeDetailsList(detailMap);
                    details.add(new Details(timestampObj, data));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Error processing details: " + ex.getMessage(), ex);
            }
        }
    }

    private static Map<String, Float> deserializeDetailsList(Map<String, Object> detailMap) {
        Map<String, Float> data = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) detailMap.get("data");

        if (dataMap != null) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                data.put(entry.getKey(), ((Number) entry.getValue()).floatValue());
            }
        }
        return data;
    }

    @Override
    public List<Details> getSensorDataByDate(String sensorId, Date selectedDate) throws ExecutionException, InterruptedException {
        try{
            DocumentReference docRef = firestore.collection(SENSOR_COLLECTION).document(sensorId);
            DocumentSnapshot snapshot = docRef.get().get();

            if (!snapshot.exists()) {
                throw new RuntimeException("Sensor with id: " + sensorId + "doesn't exist!");
            }

            SensorDTO sensorDTO = snapshot.toObject(SensorDTO.class);

            if (sensorDTO == null || sensorDTO.getDetails() == null) {
                return new ArrayList<>();
            }

            Calendar calSelected = Calendar.getInstance();
            calSelected.setTime(selectedDate);

            Calendar calDetail = Calendar.getInstance();
//            System.out.println("Sensor data for date: " + selectedDate + " fetched successfully.");
//            System.out.println("Calendar selected: " + calSelected.getTime());
            return sensorDTO.getDetails().stream()
                    .filter(detail -> {
                        if(detail.getTimestamp() == null) {
                            return false;
                        }
                        calDetail.setTimeInMillis(detail.getTimestamp().getSeconds() * 1000);
                        return calSelected.get(Calendar.YEAR) == calDetail.get(Calendar.YEAR) &&
                               calSelected.get(Calendar.MONTH) == calDetail.get(Calendar.MONTH) &&
                               calSelected.get(Calendar.DAY_OF_MONTH) == calDetail.get(Calendar.DAY_OF_MONTH);
                    })
                    .collect(Collectors.toList());
        }catch (Exception e) {
            throw new RuntimeException("Failed to fetch sensor data for date: " + selectedDate, e);
        }
    }

    @Override
    public Details getLastDetailForSensor(String sensorId){
        try{
            DocumentReference docRef = firestore.collection(SENSOR_COLLECTION).document(sensorId);
            DocumentSnapshot snapshot = docRef.get().get();

            if (!snapshot.exists()) {
                throw new RuntimeException("Sensor with id: " + sensorId + " doesn't exist!");
            }

            SensorDTO sensorDTO = snapshot.toObject(SensorDTO.class);

            if (sensorDTO == null || sensorDTO.getDetails() == null || sensorDTO.getDetails().isEmpty()) {
                System.out.println("No details found for sensor with id: " + sensorId);
                return null;
            }

            return sensorDTO.getDetails().stream()
                    .filter(d -> d.getTimestamp() != null)
                    .max(Comparator.comparing(d -> d.getTimestamp().getSeconds()))
                    .orElse(null);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
