import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import { Alert } from '../../models/alert-model';
import { AlertService } from '../../services/alert.service';
import { Room } from '../../models/room-model';
import { MatDialog } from '@angular/material/dialog';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog.component';
import {interval, Subscription, switchMap} from 'rxjs';

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
  cameraAlerts: CameraAlert[] = [];
  selectedDate: Date = new Date();
  alertSubs: { [roomId: string]: Subscription } = {};

  constructor(
    private alertService: AlertService,
    private dialog: MatDialog
  ) {}

  ngOnDestroy(): void {
    Object.values(this.alertSubs).forEach(sub => sub.unsubscribe());
  }

  ngOnInit():void{
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['rooms'] && this.rooms.length > 0) {
      this.loadAlerts();
    }
  }
  trackByRoomId(index: number, item: CameraAlert): string {
    return item.roomId;
  }

  loadAlerts() {
    const formattedDate = this.selectedDate.toDateString();

    for (const room of this.rooms) {
      const roomId = room.id;
      if (!roomId) continue;

      let existing = this.cameraAlerts.find(c => c.roomId === roomId);
      if (!existing) {
        existing = {
          roomId: room.id!,
          roomName: room.name!,
          alerts: [],
          hasNewAlert: false
        };
        this.cameraAlerts.push(existing);
      }

      this.alertService.get_alerts_by_room_id_on_a_selected_date(roomId, formattedDate).subscribe({
        next: (alerts: Alert[]) => {
          const previousLength = existing!.alerts.length;
          existing!.alerts = alerts;

          if (alerts.length > previousLength) {
            existing!.hasNewAlert = true;
          }

          console.log(`Alerts for ${room.name}:`, alerts);
        },
        error: (err) => {
          console.log(`Failed to load alerts for room ${room.name}`, err);
        }
      });

      if (this.alertSubs[roomId]) {
        this.alertSubs[roomId].unsubscribe();
      }

      // Re-subscribe to the interval for this room
      this.alertSubs[roomId] = interval(5000).pipe(
        switchMap(() =>
          this.alertService.get_alerts_by_room_id_on_a_selected_date(roomId, formattedDate)
        )
      ).subscribe({
        next: (alerts: Alert[]) => {
          const previousLength = existing!.alerts.length;
          existing!.alerts = alerts;

          if (alerts.length > previousLength) {
            existing!.hasNewAlert = true;
          }
        },
        error: (err) => {
          console.log(`Failed to update alerts for ${room.name}`, err);
        }
      });
    }

    // Filter out any camera alerts that no longer have a corresponding room
    this.cameraAlerts = this.cameraAlerts.filter(c => this.rooms.some(r => r.id === c.roomId));
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
