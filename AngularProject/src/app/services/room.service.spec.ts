/**
 * room.service.spec.ts
 * Unit tests for the RoomService.
 */
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { RoomService } from './room.service';
import { Room } from '../models/room-model';
import { Sensor } from '../models/sensor-model';

describe('RoomService', () => {
  let service: RoomService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RoomService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(RoomService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all rooms for the logged-in user', () => {
    const token = 'test-jwt';
    localStorage.setItem('jwtToken', token);

    const mockRooms: Room[] = [
      new Room('1', [], 'Living Room', 'user1')
    ];

    service.get_rooms().subscribe(rooms => {
      expect(rooms.length).toBe(1);
      expect(rooms[0].name).toBe('Living Room');
    });

    const req = httpMock.expectOne(`${BASE_URL}/room/`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    req.flush(mockRooms);
  });

  it('should fetch available rooms', () => {
    const mockRooms: Room[] = [
      new Room('2', [], 'Available Room', 'user2')
    ];

    service.get_available_rooms().subscribe(rooms => {
      expect(rooms[0].name).toBe('Available Room');
    });

    const req = httpMock.expectOne(`${BASE_URL}/room/available`);
    expect(req.request.method).toBe('GET');
    req.flush(mockRooms);
  });

  it('should assign room to user', () => {
    const payload = {
      roomId: 'room1',
      userId: 'user1',
      roomName: 'New Room',
      sensorIds: ['s1', 's2']
    };

    service.assign_room_to_user(payload).subscribe(response => {
      expect(response).toEqual({ success: true });
    });

    const req = httpMock.expectOne(`${BASE_URL}/room/assign`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ success: true });
  });

  it('should remove user from room', () => {
    const roomId = 'room123';
    const userId = 'user456';

    service.remove_user_from_room(roomId, userId).subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne(`${BASE_URL}/room/${roomId}/remove/${userId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should fetch sensors by room ID', () => {
    const roomId = 'room789';
    const sensors: Sensor[] = [
      new Sensor('s1', 'gas', 3, [], true)
    ];

    service.get_sensors_by_room_id(roomId).subscribe(data => {
      expect(data.length).toBe(1);
      expect(data[0].sensorType).toBe('gas');
    });

    const req = httpMock.expectOne(`${BASE_URL}/room/${roomId}/sensors`);
    expect(req.request.method).toBe('GET');
    req.flush(sensors);
  });

  it('should update the rooms BehaviorSubject when setRooms is called', () => {
    const mockRooms: Room[] = [
      new Room('1', [], 'Mock Room', 'userX')
    ];

    let result: Room[] | undefined;
    service.rooms$.subscribe(r => result = r);
    service.setRooms(mockRooms);

    expect(result).toEqual(mockRooms);
  });
});
