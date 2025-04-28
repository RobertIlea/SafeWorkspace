import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-room-dialog',
  standalone: false,
  
  templateUrl: './room-dialog.component.html',
  styleUrl: './room-dialog.component.css'
})
export class RoomDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data:any){}
}
