import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../models/user-model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user ID by email', () => {
    const email = 'test@example.com';
    const expectedId = 'user123';

    service.get_user_id_by_email(email).subscribe(id => {
      expect(id).toBe(expectedId);
    });

    const req = httpMock.expectOne(`${BASE_URL}/user/email/${email}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('text');
    req.flush(expectedId);
  });

  it('should get user phone by ID', () => {
    const userId = 'user123';
    const expectedPhone = '0712345678';

    service.get_user_phone_by_id(userId).subscribe(phone => {
      expect(phone).toBe(expectedPhone);
    });

    const req = httpMock.expectOne(`${BASE_URL}/user/phone?userId=${userId}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('text');
    req.flush(expectedPhone);
  });

  it('should get user by ID', () => {
    const userId = 'user123';
    const expectedUser: User = {
      id: userId,
      name: 'John',
      email: 'john@example.com',
      phone: '0712345678'
    };

    service.get_user_by_id(userId).subscribe(user => {
      expect(user).toEqual(expectedUser);
    });

    const req = httpMock.expectOne(`${BASE_URL}/user/${userId}`);
    expect(req.request.method).toBe('GET');
    req.flush(expectedUser);
  });

  it('should update user phone', () => {
    const userId = 'user123';
    const newPhone = '0798765432';
    const expectedResponse = 'Phone updated';

    service.update_user_phone(userId, newPhone).subscribe(response => {
      expect(response).toBe(expectedResponse);
    });

    const req = httpMock.expectOne(`${BASE_URL}/user/${userId}/phone?phone=${newPhone}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toBeNull();
    expect(req.request.responseType).toBe('text');
    req.flush(expectedResponse);
  });
});
