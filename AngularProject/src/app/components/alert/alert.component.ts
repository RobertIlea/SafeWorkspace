import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import { Alert } from '../../models/alert-model';
import { AlertService } from '../../services/alert.service';
import { Room } from '../../models/room-model';
import { MatDialog } from '@angular/material/dialog';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog.component';
import {catchError, forkJoin, interval, map, max, of, Subscription, switchMap} from 'rxjs';

interface CameraAlert{
  roomId: string;
  roomName: string;
  alerts: Alert[];
  hasNewAlert: boolean;
}

@Component({
  selector: 'app-alert',
  standalone: false,

  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css'
})
export class AlertComponent implements OnInit,OnDestroy,OnChanges {
  @Input() rooms: Room[] = [];
  @Output() alertsFound = new EventEmitter<boolean>()
  alertPollingSub: Subscription | undefined;
  cameraAlerts: CameraAlert[] = [];
  selectedDate: Date = new Date();
  alertSubs: { [roomId: string]: Subscription } = {};
  today: Date = new Date();

  constructor(
    private alertService: AlertService,
    private dialog: MatDialog
  ) {}

  ngOnDestroy(): void {
    Object.values(this.alertSubs).forEach(sub => sub.unsubscribe());
  }

  ngOnInit():void{
    if (this.rooms.length > 0) {
      this.loadAlerts();
      this.startAlertPolling();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['rooms'] && this.rooms.length > 0) {
      this.loadAlerts();
    }
  }

  trackByRoomId(index: number, item: CameraAlert): string {
    return item.roomId;
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  }

  loadAlerts() {
    const formattedDate = this.selectedDate.toDateString();
    const requests = this.rooms.map(room => {
      const roomId = room.id!;
      return this.alertService.get_alerts_by_room_id_on_a_selected_date(roomId, formattedDate).pipe(
        catchError(err => {
          if (err.status === 404) {
            return of([]);
          }
          console.error(`Failed to load alerts for room ${room.name}`, err);
          return of([]);
        }),
        map((alerts: Alert[]) => {
          return { room, alerts: alerts ?? [] };
        })
      );
    });

    forkJoin(requests).subscribe(results => {
      this.cameraAlerts = [];

      for (const { room, alerts } of results) {
        if (alerts.length > 0) {
          this.cameraAlerts.push({
            roomId: room.id!,
            roomName: room.name!,
            alerts: alerts,
            hasNewAlert: false
          });
        }
      }
      this.alertsFound.emit(this.cameraAlerts.length > 0);
    });
  }

  startAlertPolling(){
    if(!this.isToday(this.selectedDate)){
      return;
    }
    this.alertPollingSub = interval(5000).subscribe(() => {
      this.loadAlerts();
    })
  }

  openAlertDetails(cameraAlert: CameraAlert) {
    this.dialog.open(AlertDialogComponent, {
      width: '600px',
      data: cameraAlert
    });
    cameraAlert.hasNewAlert = false;
  }

  onDateChange(date: Date) {
    this.selectedDate = date;
    this.loadAlerts();
  }

}
