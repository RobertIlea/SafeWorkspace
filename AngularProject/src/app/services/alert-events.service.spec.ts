/**
 * alert-events.service.spec.ts
 * This file contains unit tests for the AlertEventsService.
 */
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
});
