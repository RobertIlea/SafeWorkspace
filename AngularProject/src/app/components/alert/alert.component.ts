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
  alertSub: Subscription | null = null;

  constructor(
    private alertService: AlertService,
    private dialog: MatDialog
  ) {}

  ngOnDestroy(): void {
    this.alertSub?.unsubscribe();
  }

  ngOnInit():void{
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['rooms'] && this.rooms.length > 0) {
      this.loadAlerts();
    }
  }
  loadAlerts() {
    this.cameraAlerts = [];
    const formattedDate = this.selectedDate.toDateString();

    for (const room of this.rooms) {
      const roomId = room.id;
      if (roomId) {
        this.alertService.get_alerts_by_room_id_on_a_selected_date(roomId,formattedDate).subscribe({
          next: (alerts: Alert[]) => {
            if(alerts.length === 0) {
              console.log("No alerts found for date: ", formattedDate);
              return;
            }
            const index = this.cameraAlerts.findIndex(c => c.roomId === roomId);
            if (index !== -1) {
              const previousLength = this.cameraAlerts[index].alerts.length;
              this.cameraAlerts[index].alerts = alerts;
              if(alerts.length > previousLength){
                this.cameraAlerts[index].hasNewAlert = true;
              }
            }else {
              this.cameraAlerts.push({
                roomId: room.id!,
                roomName: room.name!,
                alerts: alerts,
                hasNewAlert: false
              });
            }
            console.log("Alerts for the selected date: ", alerts)
          },
          error: (err) => {
            console.log('Failed to load alerts', err);
          }
        });

        if(this.alertSub) {
          this.alertSub.unsubscribe();
        }

        this.alertSub = interval(5000).pipe(
          switchMap(() => this.alertService.get_alerts_by_room_id_on_a_selected_date(roomId,formattedDate))).subscribe({
            next: (alerts: Alert[]) => {
              if(alerts.length === 0) {
                return;
              }
              const index = this.cameraAlerts.findIndex(c => c.roomId === roomId);
              if (index !== -1) {
                const previousLength = this.cameraAlerts[index].alerts.length;
                this.cameraAlerts[index].alerts = alerts;
                if(alerts.length > previousLength){
                  this.cameraAlerts[index].hasNewAlert = true;
                }
              } else {
                  this.cameraAlerts.push({
                    roomId: room.id!,
                    roomName: room.name!,
                    alerts: alerts,
                    hasNewAlert: false
                  });
              }
              console.log("Alerts for the selected date: ", alerts)
            },
            error: (err) => {
              console.log('Failed to load alerts', err);
            }
          }
        )
      }
    }
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
