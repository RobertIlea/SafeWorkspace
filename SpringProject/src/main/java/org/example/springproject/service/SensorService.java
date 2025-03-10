package org.example.springproject.service;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Sensor;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SensorService {
    SensorDTO addSensor(Sensor sensor);

    SensorDTO deleteSensorById(String id);

    SensorDTO updateSensor(String id, Sensor updatedSensor);

    List<SensorDTO> getSensors();

    String saveSensorData(SensorDTO sensorDTO) throws ExecutionException, InterruptedException;

    List<String> getSensorsType();

    SensorDTO getSensorById(String sensorId);

}
