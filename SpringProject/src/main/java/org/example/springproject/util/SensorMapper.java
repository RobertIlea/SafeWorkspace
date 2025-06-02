/**
 * SensorMapper.java
 * Utility class for mapping between Sensor entity and SensorDTO.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SensorMapper is a utility class that provides methods to convert between Sensor entities and SensorDTOs.
 * It handles both single objects and lists of objects, ensuring that the conversion is straightforward and efficient.
 */
public class SensorMapper {

    /**
     * Converts a SensorDTO to a Sensor entity.
     * @param sensorDTO the SensorDTO to convert
     * @return the corresponding Sensor entity, or null if sensorDTO is null
     */
    public static Sensor toEntity(SensorDTO sensorDTO){
        if(sensorDTO == null){
            return null;
        }
        return new Sensor(sensorDTO.getSensorType(), sensorDTO.getPort(), sensorDTO.getDetails());
    }

    /**
     * Converts a Sensor entity to a SensorDTO.
     * @param sensorMap the Sensor entity represented as a Map from Firestore.
     * @return the corresponding SensorDTO, or null if sensorMap is null
     */
    public static SensorDTO toDTO(Map<String, Object> sensorMap) {
        if (sensorMap == null) return null;

        return new SensorDTO(
                (String) sensorMap.get("id"),
                (String) sensorMap.get("sensorType"),
                ((Number) sensorMap.get("port")).intValue(),
                (List<Details>) sensorMap.get("details")
        );
    }

    /**
     * Converts a Sensor entity to a SensorDTO with a specified ID.
     * @param id the ID to set in the SensorDTO
     * @param sensor the Sensor entity to convert
     * @return the corresponding SensorDTO with the specified ID, or null if sensor is null
     */
    public static SensorDTO toDTO(String id, Sensor sensor){
        if(sensor == null){
            return null;
        }
        return new SensorDTO(id, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
    }

    /**
     * Converts a list of Sensor entities represented as Maps to a list of SensorDTOs.
     * @param sensorMaps the list of Sensor entities represented as Maps
     * @return a list of SensorDTOs, or an empty list if sensorMaps is null
     */
    public static List<SensorDTO> toDTOListMap(List<Map<String, Object>> sensorMaps) {
        if (sensorMaps == null) return new ArrayList<>();

        return sensorMaps.stream()
                .map(SensorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of SensorDTOs to a list of Sensor entities.
     * @param sensorDTOs the list of SensorDTOs to convert
     * @return a list of Sensor entities, or an empty list if sensorDTOs is null
     */
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

    /**
     * Converts a Sensor entity to a SensorDTO without an ID.
     * @param sensor the Sensor entity to convert
     * @return the corresponding SensorDTO without an ID, or null if sensor is null
     */
    public static SensorDTO toDTO(Sensor sensor){
        if(sensor == null){
            return null;
        }
        return new SensorDTO(null, sensor.getSensorType(), sensor.getPort(), sensor.getDetails());
    }

    /**
     * Converts a list of Sensor entities to a list of SensorDTOs.
     * @param sensors the list of Sensor entities to convert
     * @return a list of SensorDTOs, or an empty list if sensors is null
     */
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
