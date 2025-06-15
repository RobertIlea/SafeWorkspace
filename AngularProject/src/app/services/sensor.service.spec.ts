import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SensorService } from './sensor.service';
import { Sensor } from '../models/sensor-model';
import { Details } from '../models/details-model';

describe('SensorService', () => {
  let service: SensorService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SensorService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(SensorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all sensors', () => {
    const mockSensors: Sensor[] = [
      new Sensor('1', 'temperature', 2, [], true)
    ];

    service.get_sensors().subscribe(sensors => {
      expect(sensors.length).toBe(1);
      expect(sensors[0].sensorType).toBe('temperature');
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSensors);
  });

  it('should add sensor to room', () => {
    const sensor = new Sensor('2', 'gas', 3, [], true);
    const roomId = 'room123';

    service.add_sensor_to_room(sensor, roomId).subscribe(res => {
      expect(res).toEqual({});
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/${roomId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(sensor);
    req.flush({});
  });

  it('should get sensor by ID', () => {
    const sensor = new Sensor('3', 'humidity', 1, [], true);

    service.get_sensor_by_id(sensor.id!).subscribe(res => {
      expect(res).toEqual(sensor);
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/${sensor.id}`);
    expect(req.request.method).toBe('GET');
    req.flush(sensor);
  });

  it('should get sensor data by date', () => {
    const sensorId = '4';
    const dateString = '2025-06-12';
    const details = [
      new Details({ seconds: 1718188800 }, { temperature: 28 })
    ];

    service.get_sensor_data_by_date(sensorId, dateString).subscribe(data => {
      expect(data[0].data?.['temperature']).toBe(28);
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/${sensorId}/data/2025-06-12`);
    expect(req.request.method).toBe('GET');
    req.flush(details);
  });

  it('should get last sensor details', () => {
    const sensorId = '5';
    const detail = new Details({ seconds: 1718188800 }, { gas: 300 });

    service.get_last_sensor_details(sensorId).subscribe(res => {
      expect(res.data?.['gas']).toBe(300);
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/last/details/${sensorId}`);
    expect(req.request.method).toBe('GET');
    req.flush(detail);
  });

  it('should set sensor status', () => {
    const sensorId = '6';
    const status = true;

    service.set_sensor_status(sensorId, status).subscribe(res => {
      expect(res).toEqual({});
    });

    const req = httpMock.expectOne(`${BASE_URL}/sensor/${sensorId}/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ active: status });
    req.flush({});
  });
});
