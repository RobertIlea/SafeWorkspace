/**
 * room.service.ts
 * Service for managing rooms and sensors in the application.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import { Room } from '../models/room-model';
import { Sensor } from '../models/sensor-model';

/**
 * Base URL for the API.
 */
const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})

/**
 * RoomService class
 * This service provides methods to manage rooms and sensors.
 */
export class RoomService {

  /**
   * BehaviorSubject to hold the current state of rooms.
   * This allows components to subscribe and react to changes in the room list.
   */
  private roomsSubject = new BehaviorSubject<Room[]>([]);

  /**
   * Constructor for RoomService
   * @param http HttpClient instance for making HTTP requests.
   */
  constructor(private http: HttpClient) { }

  /**
   * Observable to expose the rooms state.
   * Components can subscribe to this to get updates when the room list changes.
   */
  rooms$ = this.roomsSubject.asObservable();

  /**
   * Method to set the rooms state.
   * This updates the BehaviorSubject with a new list of rooms.
   * @param rooms Array of Room objects to set as the current state.
   */
  setRooms(rooms: Room[]) {
    this.roomsSubject.next(rooms);
  }

  /**
   * Fetches all rooms associated with the logged-in user.
   * This method retrieves the rooms from the backend API and updates the BehaviorSubject.
   * @return Observable<Room[]> - An observable that emits the list of rooms.
   */
  get_rooms(): Observable<Room[]>{
    const token = localStorage.getItem('jwtToken');
    return this.http.get<Room[]>(`${BASE_URL}/room/`,{
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  /**
   * Fetches all available rooms.
   * This method retrieves the list of rooms that are currently available for assignment.
   * @return Observable<Room[]> - An observable that emits the list of available rooms.
   */
  get_available_rooms(): Observable<Room[]>{
    return this.http.get<Room[]>(`${BASE_URL}/room/available`)
  }

  /**
   * Creates a new room.
   * This method sends a POST request to the backend API to create a new room.
   * @param data - An object containing the room details.
   * @return Observable<Room> - An observable that emits the created room.
   */
  assign_room_to_user(data: {roomId: string, userId: string, roomName: string, sensorIds: string[]}): Observable<any>{
    return this.http.post<any>(`${BASE_URL}/room/assign`,data)
  }

  /**
   * Removes a user from a room.
   * @param roomId - The ID of the room from which the user will be removed.
   * @param userId - The ID of the user to be removed from the room.
   * @return Observable<any> - An observable that emits the response from the server.
   */
  remove_user_from_room(roomId:string, userId: string){
    return this.http.delete(`${BASE_URL}/room/${roomId}/remove/${userId}`);
  }

  /**
   * Fetches sensors associated with a specific room.
   * @param room_id - The ID of the room for which sensors are to be fetched.
   * @return Observable<Sensor[]> - An observable that emits the list of sensors in the specified room.
   */
  get_sensors_by_room_id(room_id:string): Observable<Sensor[]>{
    return this.http.get<Sensor[]>(`${BASE_URL}/room/${room_id}/sensors`);
  }
}
