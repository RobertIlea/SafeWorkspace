import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user-model';

const BASE_URL = 'http://localhost:8080/auth';

@Injectable({
  providedIn: 'root'
})

export class LoginService {

  constructor(private http: HttpClient) { }

  postUser(email: string, password: string): Observable<{user: User, token: string}> {
    return this.http.post<{user: User, token: string}>(`${BASE_URL}/login`, {params: {email, password}});
  }
}
