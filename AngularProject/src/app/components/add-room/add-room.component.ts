import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/room-model';
import { MatDialogRef } from '@angular/material/dialog';
import { Sensor } from '../../models/sensor-model';
import { User } from '../../models/user-model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-add-room',
  standalone: false,
  
  templateUrl: './add-room.component.html',
  styleUrl: './add-room.component.css'
})
export class AddRoomComponent implements OnInit{
  newRoomName: string = '';
  errorMessage: string = '';
  availableSensors: Sensor[] = [];
  selectedSensors: Sensor[] = [];
  availableRooms: Room[] = [];
  selectedRoom: Room | null = null;
  userId: string = '';
  constructor(
    private dialogRef: MatDialogRef<AddRoomComponent>,
    private roomService: RoomService,
    private userService: UserService,
  ){}

  ngOnInit(): void {
    this.get_available_rooms();
    const email = this.get_user_email();
    if(email){
      this.get_user_id_by_email(email);
    }
  }

  close_dialog(){
    this.dialogRef.close();
  }

  on_room_change(){
    if (this.selectedRoom) {
      this.roomService.get_sensors_by_room_id(this.selectedRoom.id!).subscribe({
        next: (sensors) => {
          this.availableSensors = sensors;
          this.selectedSensors = []; // reset selections
        },
        error: (err) => {
          console.warn("Couldn't load sensors:", err);
        }
      });
    }
  }

  // GET THE USER ID FROM localStorage //
  get_user_email():string{
    const userJson = sessionStorage.getItem("user");
    if (!userJson) {
      console.warn("User not found in sessionStorage!");
      return '';
    }

    try {
      const parsed = JSON.parse(userJson);
      return parsed.email || parsed.user_data?.email || '';
    } catch (e) {
      console.error("Error parsing user object from sessionStorage:", e);
      return '';
    }
  }

  get_user_id_by_email(email: string){
    this.userService.get_user_id_by_email(email).subscribe({
      next: (data: string) => {
        this.userId = data;
        console.log("User id that is connected: " , this.userId);  
      },
      error: (err) => {
        console.warn("Couldn't get the id from the connected user: " , err);
      }
      
    })
  }
  // --------------------------------- //

  assign_room_to_user(){
    if(!this.selectedRoom){
      this.errorMessage = "Please select a room";
      return;
    }

    if(this.selectedSensors.length === 0){
      this.errorMessage = "Please select at least one sensor";
      return;
    }

    const sensorIds = this.selectedSensors.map(sensor => sensor.id!);
    
    const payload = {
      roomId: this.selectedRoom.id!,
      userId: this.userId!, 
      roomName: this.newRoomName.trim() || this.selectedRoom.name!,
      sensorIds: sensorIds
    };

    this.roomService.assign_room_to_user(payload).subscribe({
      next: (data) => {
        console.log("Room assigned successfully!", data);
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.warn("Failed to assign room: ", err);
        this.errorMessage = err.error.message || "Failed to assign room.";
      }
    })
  }

  // GETTING THE AVAILABLE ROOMS FROM FIRESTORE //
  get_available_rooms(){
    console.log("Getting the available rooms from firestore...");
    this.roomService.get_available_rooms().subscribe({
      next: (data: Room[]) => {
        this.availableRooms = data;
        console.log("Available rooms: " , this.availableRooms);
      },
      error: (err) => {
        console.warn("Error while fetching the available rooms: " , err)
      }
    })
  }
  //---------------------------------------------//
}
