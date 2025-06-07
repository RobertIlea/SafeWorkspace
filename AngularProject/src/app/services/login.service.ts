/**
 * login.service.ts
 * Service for handling user authentication and registration.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user-model';

/**
 * Base URL for the authentication service.
 */
const BASE_URL = 'http://localhost:8080/auth';

@Injectable({
  providedIn: 'root'
})

/**
 * LoginService class
 * Handles user login and registration operations.
 */
export class LoginService {

  /**
   * Constructor for LoginService
   * @param http HttpClient instance for making HTTP requests
   */
  constructor(private http: HttpClient) { }

  /**
   * Logs in a user with email and password.
   * @param email User's email
   * @param password User's password
   * @returns Observable containing user data and token
   */
  postUser(email: string, password: string): Observable<{user: User, token: string}> {
    return this.http.post<{user: User, token: string}>(`${BASE_URL}/login`, {params: {email, password}});
  }

  /**
   * Registers a new user with email, password, and name.
   * @param email User's email
   * @param password User's password
   * @param name User's name
   * @returns Observable containing user data and token
   */
  registerUser(email: string, password: string, name: string): Observable<{ user: User, token:string }> {
    return this.http.post<{user: User, token: string}>(`${BASE_URL}/register`, {email, password, name});
  }
}
