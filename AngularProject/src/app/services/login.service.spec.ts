/**
 * login.service.spec.ts
 * Unit tests for the LoginService.
 */
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { LoginService } from './login.service';
import { User } from '../models/user-model';

describe('LoginService', () => {
  let service: LoginService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080/auth';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LoginService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(LoginService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login a user and return user and token', () => {
    const email = 'test@example.com';
    const password = 'password123';

    const mockUser = new User('1', 'Test User', email, '0712345678');
    const mockResponse = {
      user: mockUser,
      token: 'mock-jwt-token'
    };

    service.postUser(email, password).subscribe(res => {
      expect(res.user).toEqual(mockUser);
      expect(res.token).toBe('mock-jwt-token');
    });

    const req = httpMock.expectOne(`${BASE_URL}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ params: { email, password } });
    req.flush(mockResponse);
  });

  it('should register a user and return user and token', () => {
    const name = 'New User';
    const email = 'new@example.com';
    const password = 'newpass';

    const mockUser = new User('2', name, email, '0700000000');
    const mockResponse = {
      user: mockUser,
      token: 'new-token'
    };

    service.registerUser(email, password, name).subscribe(res => {
      expect(res.user).toEqual(mockUser);
      expect(res.token).toBe('new-token');
    });

    const req = httpMock.expectOne(`${BASE_URL}/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email, password, name });
    req.flush(mockResponse);
  });
});
