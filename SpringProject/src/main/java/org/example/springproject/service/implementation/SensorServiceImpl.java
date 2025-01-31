package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
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

@Service
public class SensorServiceImpl implements SensorService {

    @Autowired
    private Firestore firestore;
    private static final String SENSOR_COLLECTION = "sensors";


    @Override
    public SensorDTO addSensor(Sensor sensor){
        try{
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document();
            Map<String, Object> sensorData = getStringObjectMap(sensor);
            sensorRef.set(sensorData).get();
            return new SensorDTO(sensorRef.getId(), sensor.getSensorType(),sensor.getDetails());
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding a new sensor: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> getStringObjectMap(Sensor sensor) {
        Map<String,Object> sensorData = new HashMap<>();
        sensorData.put("sensorType", sensor.getSensorType().name());
        sensorData.put("port", sensor.getDetails().getPort());

        if(sensor.getSensorType() == SensorType.DHT22){
            sensorData.put("temperature", sensor.getDetails().getValue());
            sensorData.put("humidity", sensor.getDetails().getHumidity());
        }else{
            sensorData.put("value", sensor.getDetails().getValue());
        }
        return sensorData;
    }

    @Override
    public SensorDTO deleteSensorById(String id){
        try{
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(id);
            DocumentSnapshot sensorSnapshot = sensorRef.get().get();

            if(!sensorSnapshot.exists()){
                throw new RuntimeException("Sensor with id: " + id + "doesn't exist!");
            }
            Sensor sensor = sensorSnapshot.toObject(Sensor.class);
            sensorRef.delete().get();
            assert sensor != null;
            return new SensorDTO(id,sensor.getSensorType(),sensor.getDetails());
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the sensor: " + e.getMessage(), e);
        }
    }

    @Override
    public SensorDTO updateSensor(String id, Sensor updatedSensor){
        try{
            DocumentReference sensorRef = firestore.collection(SENSOR_COLLECTION).document(id);
            DocumentSnapshot sensorSnapshot = sensorRef.get().get();

            if(!sensorSnapshot.exists()){
                throw new RuntimeException("Sensor with id: " + id + "doesn't exist!");
            }

            Map<String, Object> currentSensor = getStringObjectMap(updatedSensor);
            sensorRef.set(currentSensor).get();
            return new SensorDTO(id,updatedSensor.getSensorType(),updatedSensor.getDetails());
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
                SensorType sensorType = SensorType.valueOf(document.getString("sensorType"));
                Integer port = Objects.requireNonNull(document.getLong("port")).intValue();
                Details details;

                if(sensorType == SensorType.DHT22){
                    float temperature = Objects.requireNonNull(document.getDouble("temperature")).floatValue();
                    float humidity = Objects.requireNonNull(document.getDouble("humidity")).floatValue();
                    details = new Details("DHT22", temperature, port);
                    details.setHumidity(humidity);
                }else {
                    float value = Objects.requireNonNull(document.getDouble("value")).floatValue();
                    details = new Details(sensorType.name(), value, port);
                }
                sensors.add(new SensorDTO(id, sensorType, details));
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
    public String saveSensorData(SensorDTO sensorDTO) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(SENSOR_COLLECTION).document(sensorDTO.getId());
        WriteResult result = docRef.set(sensorDTO).get();
        return result.getUpdateTime().toString();
    }
}
