import { Component } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/room-mopdel';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-room',
  standalone: false,
  
  templateUrl: './add-room.component.html',
  styleUrl: './add-room.component.css'
})
export class AddRoomComponent {
  newRoomName: string = '';
  errorMessage: string = '';
  
  constructor(
    private dialogRef: MatDialogRef<AddRoomComponent>,
    private roomService: RoomService,
  ){}

  close_dialog(){
    this.dialogRef.close();
  }

  add_room(){
    if(!this.newRoomName.trim()){
      this.errorMessage = 'Room cannot be empty';
      return;
    }

    const newRoom: Room = {
      id: undefined,
      name: this.newRoomName,
      sensors:[],
    }

    this.roomService.add_room(newRoom).subscribe({
      next: (createdRoom) => {
        console.log("Room added: " , createdRoom);
        this.dialogRef.close(createdRoom);
      },
      error: (error) => {
        console.log(error);
        this.errorMessage = "Failed to add a new room."
      }
    })
  }
}
