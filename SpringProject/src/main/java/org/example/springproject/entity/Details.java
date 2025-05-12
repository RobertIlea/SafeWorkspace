package org.example.springproject.entity;


import com.google.cloud.Timestamp;
import java.util.Map;

public class Details {
    private Timestamp timestamp;
    private Map<String,Float> data;

    public Details(){}

    public Details(long unixTimestamp, Map<String, Float> data) {
        this.timestamp = Timestamp.ofTimeSecondsAndNanos(unixTimestamp, 0);// Convert UNIX time
        this.data = data;
    }

    public Details(Timestamp timestampObj, Map<String, Float> data) {
        this.timestamp = timestampObj;
        this.data = data;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Float> getData() {
        return data;
    }

    public void setData(Map<String, Float> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Details{" +
                "timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
