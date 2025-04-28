import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Sensor } from '../models/sensor-model';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class SensorService {

  constructor(private http: HttpClient) { }

  get_sensors(): Observable<Sensor[]>{
    return this.http.get<Sensor[]>(`${BASE_URL}/sensor/types`);
  }

  add_sensor_to_room(sensor: Sensor, roomId: string):Observable<Object>{
    return this.http.post(`${BASE_URL}/sensor/${roomId}`,sensor);
  }

}
