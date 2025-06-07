/**
 * sensor.service.ts
 * This service handles sensor-related operations.
 */
import { HttpClient } from '@angular/common/http';
  import { Injectable } from '@angular/core';
  import { catchError, map, Observable, of } from 'rxjs';
  import { Sensor } from '../models/sensor-model';
  import { Details } from '../models/details-model';

/**
 * Base URL for the API
 */
const BASE_URL = 'http://localhost:8080';

  @Injectable({
    providedIn: 'root'
  })
  /**
   * SensorService class
   * This service provides methods to interact with sensor-related endpoints.
   */
  export class SensorService {

    /**
     * Constructor for SensorService
     * @param http - HttpClient instance for making HTTP requests
     */
    constructor(private http: HttpClient) { }

    /**
     * Get all sensors
     * @returns Observable of Sensor array
     */
    get_sensors(): Observable<Sensor[]>{
      return this.http.get<Sensor[]>(`${BASE_URL}/sensor/`);
    }

    /**
     * Get sensors by room ID
     * @param sensor - Sensor object to be added
     * @param roomId - ID of the room
     * @returns Observable of Sensor array
     */
    add_sensor_to_room(sensor: Sensor, roomId: string):Observable<Object>{
      return this.http.post(`${BASE_URL}/sensor/${roomId}`,sensor);
    }

    /**
     * Get sensor by ID
     * @param sensorId - ID of the sensor
     * @returns Observable of Sensor object
     */
    get_sensor_by_id(sensorId: string): Observable<Sensor>{
      return this.http.get<Sensor>(`${BASE_URL}/sensor/${sensorId}`);
    }

    /**
     * Get sensor data by date
     * @param sensorId - ID of the sensor
     * @param dateString - Date string in 'YYYY-MM-DD' format
     * @return Observable of Details array
     */
    get_sensor_data_by_date(sensorId: string, dateString: string):Observable<Details[]>{
      // Validate the date string format
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const correctDate = `${year}-${month}-${day}`;

      return this.http.get<Details[]>(`${BASE_URL}/sensor/${sensorId}/data/${correctDate}`);

    }

    /**
     * Get the last sensor details by sensor ID
     * @param sensorId - ID of the sensor
     * @returns Observable of Details object
     */
    get_last_sensor_details(sensorId: string): Observable<Details>{
      return this.http.get<Details>(`${BASE_URL}/sensor/last/details/${sensorId}`);
    }

    /**
     * Set the status of a sensor
     * @param sensorId - ID of the sensor
     * @param status - New status of the sensor (active/inactive)
     * @returns Observable of Object
     */
    set_sensor_status(sensorId: string, status: boolean): Observable<Object>{
      return this.http.put(`${BASE_URL}/sensor/${sensorId}/status`, { active: status });
    }
  }
