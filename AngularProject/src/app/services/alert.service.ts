import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Alert } from '../models/alert-model';
import { Observable } from 'rxjs';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor(private http: HttpClient) { }

  get_alerts_by_room_id(roomId: string):Observable<Alert[]>{
    return this.http.get<Alert[]>(`${BASE_URL}/alerts/${roomId}`)
  }

  get_alerts_by_room_id_on_a_selected_date(roomId: string, dateString: string):Observable<Alert[]>{
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const correctDate = `${year}-${month}-${day}`;

    return this.http.get<Alert[]>(`${BASE_URL}/alerts/${roomId}/data/${correctDate}`);
  }
}
