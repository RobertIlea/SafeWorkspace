/**
 * user.service.ts
 * This service handles user-related operations.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user-model';
const BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
/**
 * UserService class
 * This service provides methods to interact with user-related endpoints.
 */
export class UserService {

  /**
   * Constructor for UserService
   * @param http - HttpClient instance for making HTTP requests
   */
  constructor(private http: HttpClient) { }

  /**
   * Get user ID by email
   * @param email - User's email address
   */
  get_user_id_by_email(email: string):Observable<string>{
    return this.http.get(`${BASE_URL}/user/email/${email}`,{
          responseType: 'text'
    });
  }

  /**
   * Get user phone number by user ID
   * @param userId - User's ID
   */
  get_user_phone_by_id(userId: string): Observable<string> {
    return this.http.get(`${BASE_URL}/user/phone`, {
      params: { userId },
      responseType: 'text'
    });
  }

  /**
   * Get user details by user ID
   * @param userId - User's ID
   */
  get_user_by_id(userId: string): Observable<User> {
    return this.http.get<User>(`${BASE_URL}/user/${userId}`);
  }

  /**
   * Update user phone number
   * @param userId - User's ID
   * @param phone - New phone number
   */
  update_user_phone(userId: string, phone: string): Observable<string> {
    return this.http.put(`${BASE_URL}/user/${userId}/phone`, null, {
      params: { phone },
      responseType: 'text'
    });
  }

}
