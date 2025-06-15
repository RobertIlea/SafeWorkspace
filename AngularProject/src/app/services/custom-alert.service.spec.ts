/**
 * custom-alert.service.spec.ts
 * This file contains unit tests for the CustomAlertService.
 */
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';
import { CustomAlertService } from './custom-alert.service';
import { CustomAlert } from '../models/customAlert-model';

describe('CustomAlertService', () => {
  let service: CustomAlertService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080';

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        CustomAlertService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CustomAlertService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a custom alert', () => {
    const alert = new CustomAlert('user1', 'room1', 'sensor1', 'temperature', 'temperature', '>', 30, 'Too hot!');
    const expectedResponse = { ...alert, id: 'alert123' };

    service.create_custom_alert(alert).subscribe(res => {
      expect(res).toEqual(jasmine.objectContaining(expectedResponse));
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(alert);
    req.flush(expectedResponse);
  });

  it('should get all custom alerts for logged-in user', () => {
    const token = 'jwt-token';
    localStorage.setItem('jwtToken', token);

    const alerts = [
      new CustomAlert('user1', 'room1', 'sensor1', 'temperature', 'temperature', '>', 25, 'Warning!')
    ];

    service.get_all_custom_alerts_based_on_logged_user().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].message).toBe('Warning!');
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    req.flush(alerts);
  });

  it('should get custom alerts by user ID', () => {
    const userId = 'user123';
    const alerts = [
      new CustomAlert(userId, 'room1', 'sensor2', 'humidity', 'humidity', '<', 40, 'Too dry!')
    ];

    service.get_custom_alert_by_user_id(userId).subscribe(res => {
      expect(res[0].sensorType).toBe('humidity');
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/${userId}`);
    expect(req.request.method).toBe('GET');
    req.flush(alerts);
  });

  it('should delete a custom alert by ID', () => {
    const alertId = 'alert456';
    const alert = new CustomAlert('user1', 'room1', 'sensor1', 'gas', 'gas', '>', 200, 'Gas leak!');
    alert.id = alertId;

    service.delete_custom_alert(alertId).subscribe(res => {
      expect(res).toEqual(alert);
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/${alertId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(alert);
  });

  it('should get a custom alert by ID', () => {
    const alertId = 'alert789';
    const alert = new CustomAlert('user2', 'room2', 'sensor3', 'fire', 'flame', '=', 1, 'Fire detected!');
    alert.id = alertId;

    service.get_custom_alert_by_id(alertId).subscribe(res => {
      expect(res).toEqual(alert);
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/${alertId}`);
    expect(req.request.method).toBe('GET');
    req.flush(alert);
  });

  it('should update a custom alert by ID', () => {
    const alertId = 'alert999';
    const updatedAlert = new CustomAlert('user3', 'room3', 'sensor4', 'smoke', 'smoke', '<', 100, 'Low smoke level');
    updatedAlert.id = alertId;

    service.update_custom_alert(alertId, updatedAlert).subscribe(res => {
      expect(res).toEqual(updatedAlert);
    });

    const req = httpMock.expectOne(`${BASE_URL}/custom-alert/${alertId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedAlert);
    req.flush(updatedAlert);
  });
});
