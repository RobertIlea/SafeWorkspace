package org.example.springproject.util;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SensorMapper {

    public static Sensor toEntity(SensorDTO sensorDTO){
        if(sensorDTO == null){
            return null;
        }
        return new Sensor(sensorDTO.getSensorType(), sensorDTO.getPort(), sensorDTO.getDetails());
    }

    public static SensorDTO toDTO(Map<String, Object> sensorMap) {
        if (sensorMap == null) return null;

        return new SensorDTO(
                (String) sensorMap.get("id"),  // Extract ID from Firestore map
                (String) sensorMap.get("sensorType"),
                ((Number) sensorMap.get("port")).intValue(),
                (List<Details>) sensorMap.get("details")
        );
    }
    public static SensorDTO toDTO(String id, Sensor sensor){
        if(sensor == null){
            return null;
        }
        return new SensorDTO(id, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
    }

    public static List<SensorDTO> toDTOListMap(List<Map<String, Object>> sensorMaps) {
        if (sensorMaps == null) return new ArrayList<>();

        return sensorMaps.stream()
                .map(SensorMapper::toDTO)
                .collect(Collectors.toList());
    }
    public static List<Sensor> toEntityList(List<SensorDTO> sensorDTOs){
        if (sensorDTOs == null) {
            return new ArrayList<>();
        }
        List<Sensor> sensors = new ArrayList<>();
        for (SensorDTO sensorDTO : sensorDTOs) {
            sensors.add(toEntity(sensorDTO));
        }
        return sensors;
    }
    public static SensorDTO toDTO(Sensor sensor){
        if(sensor == null){
            return null;
        }
        return new SensorDTO(null, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
    }
    public static List<SensorDTO> toDTOList(List<Sensor> sensors){
        if (sensors == null) {
            return new ArrayList<>();
        }
        List<SensorDTO> sensorDTOs = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensorDTOs.add(toDTO(sensor));
        }
        return sensorDTOs;
    }
}
