/**
 * alert.service.spec.ts
 * This file contains unit tests for the AlertService.
 */
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AlertService } from './alert.service';
import { Alert } from '../models/alert-model';

describe('AlertService', () => {
  let service: AlertService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AlertService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AlertService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch alerts by room ID', () => {
    const roomId = 'room1';
    const mockAlerts: Alert[] = [
      new Alert('a1', roomId, 's1', { seconds: 1710000000 }, 'temperature', { temperature: 35 }, 'Overheating!')
    ];

    service.get_alerts_by_room_id(roomId).subscribe(alerts => {
      expect(alerts.length).toBe(1);
      expect(alerts[0].message).toBe('Overheating!');
    });

    const req = httpMock.expectOne(`${BASE_URL}/alerts/${roomId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAlerts);
  });

  it('should fetch alerts by room ID and date', () => {
    const roomId = 'room2';
    const dateString = '2025-06-12';
    const expectedDate = '2025-06-12';
    const mockAlerts: Alert[] = [
      new Alert('a2', roomId, 's2', { seconds: 1710000000 }, 'humidity', { humidity: 20 }, 'Too dry!')
    ];

    service.get_alerts_by_room_id_on_a_selected_date(roomId, dateString).subscribe(alerts => {
      expect(alerts[0].sensorType).toBe('humidity');
    });

    const req = httpMock.expectOne(`${BASE_URL}/alerts/${roomId}/data/${expectedDate}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAlerts);
  });
});
