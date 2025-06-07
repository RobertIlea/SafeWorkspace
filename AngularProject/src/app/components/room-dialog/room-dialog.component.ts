import {ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from '@angular/material/dialog';
import {RoomService} from '../../services/room.service';
import {Sensor} from '../../models/sensor-model';
import {Details} from '../../models/details-model';
import {distinctUntilChanged, Subscription} from 'rxjs';
import {CustomAlertDialogComponent} from '../custom-alert-dialog/custom-alert-dialog.component';
import {Room} from '../../models/room-model';
import {Alert} from '../../models/alert-model';
import {SensorService} from '../../services/sensor.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-room-dialog',
  standalone: false,

  templateUrl: './room-dialog.component.html',
  styleUrl: './room-dialog.component.css'
})
export class RoomDialogComponent implements OnInit,OnDestroy {
  private roomSub: Subscription | null = null;
  room: Room | null = null;
  alerts: Alert[] = [];
  constructor(@Inject(MAT_DIALOG_DATA) public data:any, private roomService: RoomService,  private cdr: ChangeDetectorRef,  private dialog: MatDialog, private sensorService: SensorService, private snackBar: MatSnackBar) {}


  ngOnInit() {
    this.room = this.data.room;
    this.alerts = [...this.data.room.alerts || []];

    this.roomSub = this.roomService.rooms$.pipe(
      distinctUntilChanged((prev, curr) => {
        const currentRoom = curr.find(r => r.id === this.data.room.id);
        return JSON.stringify(currentRoom) === JSON.stringify(this.room);
      })
    ).subscribe(rooms => {
      const updatedRoom = rooms.find(room => room.id === this.data.room.id);
      if (updatedRoom) {
        this.room = {...updatedRoom};
        if (this.data.alerts && this.data.alerts.length > 0) {
          this.alerts = [...this.data.alerts];
        }
        this.cdr.detectChanges();
      }
    });
  }


  ngOnDestroy() {
    this.roomSub?.unsubscribe();
  }

  getLastDetail(sensor: Sensor): Details | null {
    if (!sensor.details || sensor.details.length === 0) return null;
    return sensor.details[sensor.details.length - 1];
  }

  getGasLevelMessage(value: number): string {
    if (value < 200) return "Excellent air quality rate";
    if (value < 400) return "Low gas presence";
    if (value < 700) return "Moderate gas level";
    return "Dangerous gas level!";
  }

  getMq2LevelMessage(value: number): string {
    if (value < 200) return "No smoke/fuel gas present";
    if (value < 400) return "Low smoke/fuel gas presence";
    if (value < 700) return "Moderate smoke/fuel gas presence";
    return "Dangerous smoke/fuel gas level!";
  }

  getCircleClass(key: string, value: number): string {
    if(key === 'temperature'){
      if(value < 10) return 'sensor-circle temperature-critical';
      if(value <= 30) return 'sensor-circle temperature-normal';
      return 'sensor-circle temperature-warning';
    }

    if(key === 'humidity'){
      if(value < 30) return 'sensor-circle humidity-critical';
      if(value <= 75) return 'sensor-circle humidity-normal';
      return 'sensor-circle humidity-warning';
    }

    if(key === 'gas'){
      if(value < 10) return 'sensor-circle gas-critical';
      if(value < 400) return 'sensor-circle gas-normal';
      if(value < 700) return 'sensor-circle gas-detected';
      return 'sensor-circle gas-critical';
    }

    if(key === 'mq2Value'){
      if(value < 600) return 'sensor-circle gas-normal';
      if(value < 1000) return 'sensor-circle gas-detected';
      return 'sensor-circle gas-critical';
    }

    return 'sensor-circle';
  }

  openCustomAlertDialog(sensor:Sensor): void {
    this.dialog.open(CustomAlertDialogComponent, {
      width: '500px',
      data: {
        room: this.data.room,
        sensor: sensor,
        userId: this.data.room.userId,
      }
    })
  }

  toggleSensorStatus(sensor: any): void {
    const newStatus = !sensor.active;
    this.sensorService.set_sensor_status(sensor.id, newStatus).subscribe({
      next: () => {
        sensor.active = newStatus;
        this.snackBar.open(`Sensor ${newStatus ? 'activated' : 'deactivated'} successfully.`, 'Close', {
          duration: 3000
        });
      },
      error: (err) => {
        this.snackBar.open(`Failed to update sensor status.`, 'Close', {
          duration: 3000
        });
        console.error(err);
      }
    });
  }
}
