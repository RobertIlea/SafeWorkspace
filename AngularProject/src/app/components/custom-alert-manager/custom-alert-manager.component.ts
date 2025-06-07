import {Component, OnInit} from '@angular/core';
import {CustomAlert} from '../../models/customAlert-model';
import {CustomAlertService} from '../../services/custom-alert.service';
import {EditCustomAlertDialogComponent} from '../edit-custom-alert-dialog/edit-custom-alert-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {RoomService} from '../../services/room.service';
import {Room} from '../../models/room-model';
import {AlertEventsService} from '../../services/alert-events.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-custom-alert-manager',
  standalone: false,

  templateUrl: './custom-alert-manager.component.html',
  styleUrl: './custom-alert-manager.component.css'
})
export class CustomAlertManagerComponent implements OnInit {
  customAlerts: CustomAlert[] = [];
  filteredAlerts: CustomAlert[] = [];
  selectedRoomId: string = '';
  uniqueRooms: { id: string, name: string }[] = [];
  roomNamesMap: { [roomId: string]: string } = {};
  sensorTypesMap: { [sensorId: string]: string } = {};
  expandedText: { [id: string]: boolean } = {};

  constructor(
    private customAlertService: CustomAlertService,
    private alertEventsService: AlertEventsService,
    private dialog: MatDialog,
    private roomService: RoomService,
    private snackBar: MatSnackBar,
  ){ }

  ngOnInit(): void {
    this.alertEventsService.customAlertCreated$.subscribe(() => {
      this.fetchAlerts();
    })

    this.roomService.rooms$.subscribe(rooms => {
      this.buildRoomAndSensorMaps(rooms);
      this.fetchAlerts();
    });
  }

  isExpanded(id: string): boolean {
    return this.expandedText[id];
  }

  toggleExpand(id: string): void {
    this.expandedText[id] = !this.expandedText[id];
  }

  buildRoomAndSensorMaps(rooms: Room[]) {
    this.roomNamesMap = {};
    this.sensorTypesMap = {};
    rooms.forEach(room => {
      if (room.name != null) {
        this.roomNamesMap[room.id!] = room.name;
      }
      room.sensors?.forEach(sensor => {
        if (sensor.sensorType != null) {
          this.sensorTypesMap[sensor.id!] = sensor.sensorType;
        }
      });
    });
  }

  fetchAlerts() {
    this.customAlertService.get_all_custom_alerts_based_on_logged_user().subscribe({
      next: (alerts: CustomAlert[]) => {
        if(!alerts || alerts.length === 0) {
          alerts = [];
        }
        this.customAlerts = alerts;
        this.filteredAlerts = alerts;

        const seen: { [key: string]: boolean } = {};
        this.uniqueRooms = alerts
          .filter(a => a.roomId && !seen[a.roomId] && (seen[a.roomId] = true))
          .map(a => ({ id: a.roomId!, name: this.roomNamesMap[a.roomId!] || 'Room ' + a.roomId!.substring(0, 5) }));
      },
      error: (err) => {
        if(err.status === 404) {
          this.customAlerts = this.customAlerts.filter(alert => alert.roomId !== this.selectedRoomId);
        }
      }
    });
  }

  editAlert(alert: CustomAlert) {
    const dialogRef = this.dialog.open(EditCustomAlertDialogComponent, {
      width: '400px',
      data: alert
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fetchAlerts();
      }
    });
  }

  deleteAlert(alert: CustomAlert) {
    if (alert.roomId != null) {
      this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: {
          title: 'Delete Custom Alert',
          message: `Are you sure you want to delete the custom alert for ${this.getRoomName(alert.roomId)}?`
        }
      }).afterClosed().subscribe(confirmed => {
        if (confirmed) {
          this.customAlertService.delete_custom_alert(alert.id!).subscribe({
            next: () => {
              this.snackBar.open('Custom alert deleted successfully', 'Close', {duration: 3000, panelClass: ['snackbar-success']});
              this.fetchAlerts();
            },
            error: () => {
              this.snackBar.open('Failed to delete custom alert', 'Close', {duration: 3000, panelClass: ['snackbar-error']});
            }
          });
        }
      })
    }
  }

  getRoomName(roomId: string): string {
    return this.roomNamesMap[roomId] || roomId;
  }

  getSensorType(sensorId: string): string {
    return this.sensorTypesMap[sensorId] || sensorId;
  }

  applyFilters() {
    if (!this.selectedRoomId) {
      this.filteredAlerts = this.customAlerts;
    } else {
      this.filteredAlerts = this.customAlerts.filter(
        alert => alert.roomId === this.selectedRoomId
      );
    }
  }
}
