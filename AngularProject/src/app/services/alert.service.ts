/**
 * alert.service.ts
 * Service for managing alerts in the application.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Alert } from '../models/alert-model';
import { Observable } from 'rxjs';

/**
 * Base URL for the API.
 */
const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})

/**
 * AlertService class
 * This service provides methods to interact with the alert API.
 */
export class AlertService {

  /**
   * Constructor for AlertService
   * @param http HttpClient instance for making HTTP requests
   */
  constructor(private http: HttpClient) { }

  /**
   * Fetches all alerts from the API.
   * @param roomId The ID of the room to fetch alerts for
   * @returns Observable of Alert array
   */
  get_alerts_by_room_id(roomId: string):Observable<Alert[]>{
    return this.http.get<Alert[]>(`${BASE_URL}/alerts/${roomId}`)
  }

  /**
   * Fetches alerts for a specific room on a selected date.
   * @param roomId The ID of the room to fetch alerts for.
   * @param dateString The date string in 'YYYY-MM-DD' format.
   */
  get_alerts_by_room_id_on_a_selected_date(roomId: string, dateString: string):Observable<Alert[]>{
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const correctDate = `${year}-${month}-${day}`;

    return this.http.get<Alert[]>(`${BASE_URL}/alerts/${roomId}/data/${correctDate}`);
  }
}
