package org.example.springproject.util;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Sensor;

public class SensorMapper {
    public static Sensor toEntity(SensorDTO sensorDTO){
        if(sensorDTO == null){
            return null;
        }
        return new Sensor(sensorDTO.getSensorType(),sensorDTO.getPort(),sensorDTO.getDetails());
    }
    public static SensorDTO toDTO(String id, Sensor sensor){
        if(sensor == null){
            return null;
        }
        return new SensorDTO(id, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
    }
}
