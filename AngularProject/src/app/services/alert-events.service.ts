/**
 * alert-events.service.ts
 * This service is used to manage events related to custom alerts.
 */
import { Injectable } from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})

/**
 * AlertEventsService class
 * This service provides a BehaviorSubject to notify when a custom alert is created.
 */
export class AlertEventsService {

  /**
   * customAlertCreated$ is a BehaviorSubject that emits when a custom alert is created.
   */
  customAlertCreated$ = new BehaviorSubject<void>(undefined);

  /**
   * Empty constructor for AlertEventsService.
   */
  constructor() { }
}
