import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import {RoomService} from '../../services/room.service';
import {Sensor} from '../../models/sensor-model';
import {Details} from '../../models/details-model';

@Component({
  selector: 'app-room-dialog',
  standalone: false,

  templateUrl: './room-dialog.component.html',
  styleUrl: './room-dialog.component.css'
})
export class RoomDialogComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public data:any, private roomService: RoomService,  private cdr: ChangeDetectorRef){}

  ngOnInit() {
    this.roomService.rooms$.subscribe(rooms => {
      const updatedRoom = rooms.find(room => room.id === this.data.room.id);
      if(updatedRoom){
        Object.assign(this.data, updatedRoom);
        this.cdr.detectChanges();
      }
    })
  }

  getLastDetail(sensor: Sensor): Details | null {
    if (!sensor.details || sensor.details.length === 0) return null;
    return sensor.details[sensor.details.length - 1];
  }

  getGasLevelMessage(value: number): string {
    if (value < 200) return "Air quality: Excellent";
    if (value < 400) return "Low gas presence";
    if (value < 700) return "Moderate gas level";
    return "Dangerous gas level!";
  }


}
