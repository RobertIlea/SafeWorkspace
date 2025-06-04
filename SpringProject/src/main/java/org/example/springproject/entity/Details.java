/**
 * Details.java
 * This class represents the details of a sensor reading.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.entity;

import com.google.cloud.Timestamp;
import java.util.Map;

/**
 * Details class encapsulates the timestamp and data of a sensor reading.
 */
public class Details {

    /**
     * The timestamp of the sensor reading.
     */
    private Timestamp timestamp;

    /**
     * The data of the sensor reading, where the key is the sensor name or type and the value is the sensor reading.
     */
    private Map<String,Float> data;

    /**
     * Default constructor for Details.
     * Initializes an empty Details object.
     */
    public Details(){}

    /**
     * Constructor for Details.
     * Initializes the Details object with a UNIX timestamp and a map of sensor data.
     * This constructor converts the UNIX timestamp (in seconds) to a Timestamp object.
     * @param unixTimestamp The UNIX timestamp in seconds.
     * @param data A map containing sensor names/types as keys and their readings as values.
     */
    public Details(long unixTimestamp, Map<String, Float> data) {
        this.timestamp = Timestamp.ofTimeSecondsAndNanos(unixTimestamp, 0);// Convert UNIX time
        this.data = data;
    }

    /**
     * Constructor for Details.
     * Initializes the Details object with a Timestamp object and a map of sensor data.
     * This constructor is useful when the timestamp is already in the form of a Timestamp object.
     * @param timestampObj The Timestamp object representing the time of the sensor reading.
     * @param data A map containing sensor names/types as keys and their readings as values.
     */
    public Details(Timestamp timestampObj, Map<String, Float> data) {
        this.timestamp = timestampObj;
        this.data = data;
    }

    /**
     * Gets the timestamp of the sensor reading.
     * @return The timestamp of the sensor reading.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the sensor reading.
     * @param timestamp The timestamp to set for the sensor reading.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the data of the sensor reading.
     * @return A map containing sensor names/types as keys and their readings as values.
     */
    public Map<String, Float> getData() {
        return data;
    }

    /**
     * Sets the data of the sensor reading.
     * @param data A map containing sensor names/types as keys and their readings as values.
     */
    public void setData(Map<String, Float> data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the Details object.
     * @return A string containing the timestamp and data of the sensor reading.
     */
    @Override
    public String toString() {
        return "Details{" +
                "timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
