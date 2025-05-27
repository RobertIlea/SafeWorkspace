import {Component, OnInit} from '@angular/core';
import {CustomAlert} from '../../models/customAlert-model';
import {CustomAlertService} from '../../services/custom-alert.service';
import {EditCustomAlertDialogComponent} from '../edit-custom-alert-dialog/edit-custom-alert-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {RoomService} from '../../services/room.service';
import {Room} from '../../models/room-model';

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


  constructor(private customAlertService: CustomAlertService, private dialog: MatDialog, private roomService: RoomService) { }

  ngOnInit(): void {
    this.roomService.rooms$.subscribe(rooms => {
      this.buildRoomAndSensorMaps(rooms);
      this.fetchAlerts();
    });
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
    this.customAlertService.get_all_custom_alerts().subscribe({
      next: (alerts: CustomAlert[]) => {
        this.customAlerts = alerts;
        this.filteredAlerts = alerts;

        const seen: { [key: string]: boolean } = {};
        this.uniqueRooms = alerts
          .filter(a => a.roomId && !seen[a.roomId] && (seen[a.roomId] = true))
          .map(a => ({ id: a.roomId!, name: this.roomNamesMap[a.roomId!] || 'Room ' + a.roomId!.substring(0, 5) }));
      },
      error: (err) => {
        console.error('Failed to load custom alerts:', err);
      }
    });
  }

  editAlert(alert: CustomAlert) {
    const dialogRef = this.dialog.open(EditCustomAlertDialogComponent, {
      width: '500px',
      data: alert
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fetchAlerts();
      }
    });
  }

  deleteAlert(alert: CustomAlert) {
    if (confirm('Are you sure you want to delete this alert?')) {
      this.customAlertService.delete_custom_alert(alert.id!).subscribe({
        next: () => this.fetchAlerts(),
        error: (err) => console.error('Failed to delete alert:', err)
      });
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
