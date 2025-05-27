import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CustomAlert} from '../models/customAlert-model';
import {Observable} from 'rxjs';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class CustomAlertService {

  constructor(private http: HttpClient) { }

  create_custom_alert(alert: CustomAlert): Observable<CustomAlert> {
    return this.http.post<CustomAlert>(`${BASE_URL}/custom-alert/`, alert);
  }

  get_all_custom_alerts(): Observable<CustomAlert[]> {
    return this.http.get<CustomAlert[]>(`${BASE_URL}/custom-alert/`);
  }

  get_custom_alert_by_user_id(userId: string): Observable<CustomAlert[]> {
    return this.http.get<CustomAlert[]>(`${BASE_URL}/custom-alert/${userId}`);
  }

  delete_custom_alert(alertId: string): Observable<CustomAlert> {
    return this.http.delete<CustomAlert>(`${BASE_URL}/custom-alert/${alertId}`);
  }

  get_custom_alert_by_id(id: string): Observable<CustomAlert> {
    return this.http.get<CustomAlert>(`${BASE_URL}/custom-alert/${id}`);
  }

  update_custom_alert(alertId: string, customAlert: CustomAlert): Observable<CustomAlert> {
    return this.http.put<CustomAlert>(`${BASE_URL}/custom-alert/${alertId}`, customAlert);
  }
}
