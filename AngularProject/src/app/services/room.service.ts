import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Room } from '../models/room-mopdel';
import { Sensor } from '../models/sensor-model';
// import { decodeJwtToken } from '../components/utils/jwt-utils';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  constructor(private http: HttpClient) { }

  get_rooms_by_user_id(id: string): Observable<Room[]>{
    // const token = localStorage.getItem('jwtToken');

    return this.http.get<Room[]>(`${BASE_URL}/room/${id}`);
    
  }

  add_room(room: Room): Observable<Room>{
    const token = localStorage.getItem('jwtToken');
    
    return this.http.post<Room>(`${BASE_URL}/room/`,room,{headers: new HttpHeaders({
      'Authorization': `Bearer ${token}` 
    })});
  }
  get_sensors_by_room_id(room_id:string): Observable<Sensor[]>{
    return this.http.get<Sensor[]>(`${BASE_URL}/room/${room_id}/sensors`);
  }
}
