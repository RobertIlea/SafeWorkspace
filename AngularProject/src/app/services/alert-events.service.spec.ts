import { TestBed } from '@angular/core/testing';
import { AlertEventsService } from './alert-events.service';

describe('AlertEventsService', () => {
  let service: AlertEventsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlertEventsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit a value when custom alert is created', (done) => {
    let emitted = false;

    service.customAlertCreated$.subscribe(value => {
      emitted = true;
      expect(value).toBeUndefined();
      done();
    });

    expect(emitted).toBeTrue();
  });

  it('should emit new value when next() is called', (done) => {
    let callCount = 0;

    const sub = service.customAlertCreated$.subscribe(() => {
      callCount++;
      if (callCount === 2) {
        done();
      }
    });

    service.customAlertCreated$.next();
  });
});
