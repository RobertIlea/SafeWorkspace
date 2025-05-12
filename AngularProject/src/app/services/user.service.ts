import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  // GET USER ID BASED ON IT'S EMAIL
  get_user_id_by_email(email: string):Observable<string>{
    return this.http.get(`${BASE_URL}/user/email/${email}`,{
          responseType: 'text'
    });
  }
}
