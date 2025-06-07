/**
 * custom-alert.service.ts
 * This service handles operations related to custom alerts.
 */
import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CustomAlert} from '../models/customAlert-model';
import {Observable} from 'rxjs';

/**
 * Base url for the API endpoints.
 */
const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})

/**
 * CustomAlertService class
 * Handles operations related to custom alerts.
 */
export class CustomAlertService {

  /**
   * Constructor for CustomAlertService
   * @param http HttpClient instance for making HTTP requests
   */
  constructor(private http: HttpClient) { }

  /**
   * Creates a new custom alert.
   * @param alert The custom alert to be created.
   * @returns Observable containing the created custom alert.
   */
  create_custom_alert(alert: CustomAlert): Observable<CustomAlert> {
    return this.http.post<CustomAlert>(`${BASE_URL}/custom-alert/`, alert);
  }

  /**
   * Retrieves all custom alerts based on the logged-in user.
   * @returns Observable containing an array of custom alerts.
   */
  get_all_custom_alerts_based_on_logged_user(): Observable<CustomAlert[]> {
    const token = localStorage.getItem('jwtToken');
    return this.http.get<CustomAlert[]>(`${BASE_URL}/custom-alert/`,{
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  /**
   * Retrieves custom alerts by user ID.
   * @param userId The ID of the user whose custom alerts are to be retrieved.
   * @returns Observable containing an array of custom alerts for the specified user.
   */
  get_custom_alert_by_user_id(userId: string): Observable<CustomAlert[]> {
    return this.http.get<CustomAlert[]>(`${BASE_URL}/custom-alert/${userId}`);
  }

  /**
   * Deletes a custom alert by its ID.
   * @param alertId The ID of the custom alert to be deleted.
   * @returns Observable containing the deleted custom alert.
   */
  delete_custom_alert(alertId: string): Observable<CustomAlert> {
    return this.http.delete<CustomAlert>(`${BASE_URL}/custom-alert/${alertId}`);
  }

  /**
   * Retrieves a custom alert by its ID.
   * @param id The ID of the custom alert to be retrieved.
   * @returns Observable containing the custom alert with the specified ID.
   */
  get_custom_alert_by_id(id: string): Observable<CustomAlert> {
    return this.http.get<CustomAlert>(`${BASE_URL}/custom-alert/${id}`);
  }

  /**
   * Updates a custom alert by its ID.
   * @param alertId The ID of the custom alert to be updated.
   * @param customAlert The updated custom alert data.
   * @returns Observable containing the updated custom alert.
   */
  update_custom_alert(alertId: string, customAlert: CustomAlert): Observable<CustomAlert> {
    return this.http.put<CustomAlert>(`${BASE_URL}/custom-alert/${alertId}`, customAlert);
  }
}
