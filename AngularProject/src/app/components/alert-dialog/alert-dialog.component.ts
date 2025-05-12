import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Alert } from '../../models/alert-model';

@Component({
  selector: 'app-alert-dialog',
  standalone: false,
  
  templateUrl: './alert-dialog.component.html',
  styleUrl: './alert-dialog.component.css'
})
export class AlertDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { roomId: string, roomName: string, alerts: Alert[] }){}

  get_badge_class(value: number, type: 'temperature' | 'humidity'):string{
    if(type == 'temperature'){
      if(value > 20){
        return 'badge-high'
      }
    }else if(type === 'humidity'){
      if(value > 40){
        return 'badge-high'
      }
    }
    return 'badge-normal';
  }
}
