import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { Sensor } from '../models/sensor-model';
import { Details } from '../models/details-model';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class SensorService {

  constructor(private http: HttpClient) { }

  get_sensors(): Observable<Sensor[]>{
    return this.http.get<Sensor[]>(`${BASE_URL}/sensor/`);
  }

  add_sensor_to_room(sensor: Sensor, roomId: string):Observable<Object>{
    return this.http.post(`${BASE_URL}/sensor/${roomId}`,sensor);
  }

  get_sensor_by_id(sensorId: string): Observable<Sensor>{
    return this.http.get<Sensor>(`${BASE_URL}/sensor/${sensorId}`);
  }

  get_sensor_data_by_date(sensorId: string, dateString: string):Observable<Details[]>{
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const correctDate = `${year}-${month}-${day}`;

    return this.http.get<Details[]>(`${BASE_URL}/sensor/${sensorId}/data/${correctDate}`);

  }
}
