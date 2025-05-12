import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import { Room } from '../models/room-model';
import { Sensor } from '../models/sensor-model';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private roomSubject = new BehaviorSubject<Room[]>([]);
  constructor(private http: HttpClient) { }


  rooms$ = this.roomSubject.asObservable();
  setRooms(rooms: Room[]) {
    this.roomSubject.next(rooms);
  }
  // Get all the rooms based on logged user
  get_rooms(): Observable<Room[]>{
    const token = localStorage.getItem('jwtToken');

    return this.http.get<Room[]>(`${BASE_URL}/room/`,{
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Get the available rooms from firestore
  get_available_rooms(): Observable<Room[]>{
    return this.http.get<Room[]>(`${BASE_URL}/room/available`)
  }

  // Assign a room to the user
  assign_room_to_user(data: {roomId: string, userId: string, roomName: string, sensorIds: string[]}): Observable<any>{
    return this.http.post<any>(`${BASE_URL}/room/assign`,data)
  }

  // Free a used room
  remove_user_from_room(roomId:string, userId: string){
    return this.http.delete(`${BASE_URL}/room/${roomId}/remove/${userId}`);
  }

  get_sensors_by_room_id(room_id:string): Observable<Sensor[]>{
    return this.http.get<Sensor[]>(`${BASE_URL}/room/${room_id}/sensors`);
  }
}
