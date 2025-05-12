import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/room-model';
import { SensorService } from '../../services/sensor.service';
import { Sensor } from '../../models/sensor-model';
import { trigger, style, animate, transition } from '@angular/animations';
import { MatDialog } from '@angular/material/dialog';
import { RoomDialogComponent } from '../room-dialog/room-dialog.component';
import { AddRoomComponent } from '../add-room/add-room.component';
import { ChartData, ChartOptions } from 'chart.js';
import { Details } from '../../models/details-model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import {interval, Subscription, switchMap} from 'rxjs';
import { BaseChartDirective } from 'ng2-charts';


@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class DashboardComponent implements OnInit, OnDestroy{
  @ViewChild('realtimeChart') chart?:BaseChartDirective;
  rooms: Room[] = [];
  selectedRoom: Room | null = null;
  selectedSensor: Sensor | null = null;
  selectedDate: Date = new Date();
  hasDataForChart: boolean = true;
  sensorSub: Subscription | null = null;

  constructor(
    private roomService: RoomService,
    private sensorService: SensorService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ){}

  ngOnInit(): void {
      this.get_rooms();
      this.prepare_chart_data();
  }
  ngOnDestroy() {
    this.sensorSub?.unsubscribe();
  }
  getGasLevelMessage(value: number): string {
    if (value < 200) return "Air quality: Excellent";
    if (value < 400) return "Low gas presence";
    if (value < 700) return "Moderate gas level";
    return "Dangerous gas level!";
  }
  // Card 3
  get_rooms(): void {
    this.roomService.get_rooms().subscribe({
      next: (room: Room[]) => {
        this.rooms = room;
        this.roomService.setRooms(room);

        console.log("Rooms: " , this.rooms[0]);
        console.log("Sensors: ", this.rooms[0].sensors);

        if(this.rooms.length > 0){
          this.selectedRoom = this.rooms[0];
          this.selectedSensor = this.selectedRoom?.sensors?.[0] ?? null;
          this.prepare_chart_data();
        }
      },
      error: (error) => {
        console.error(`Failed to fetch rooms`, error);
      }
    });
  }

  open_add_room_dialog():void{
    const dialogRef = this.dialog.open(AddRoomComponent,{
      width: '400px'
    })

    dialogRef.afterClosed().subscribe((newRoom) => {
      if(newRoom){
        this.rooms.push(newRoom);
        console.log("Updated rooms: ", this.rooms);
      }
    })
  }

  open_room_dialog(room: Room){
    this.dialog.open(RoomDialogComponent, {
      width: '600px',
      data: { room }
    });
  }

  confirm_remove(room: Room, event: MouseEvent){
      event.stopPropagation(); // Prevent the open of the dialog

      this.dialog.open(ConfirmDialogComponent, {
        width: '300px',
        data: {
          title: 'Confirm Delete',
          message: `Are you sure you want to remove "${room.name}"?`
        }
      }).afterClosed().subscribe(result => {
        if (result === true) {
           if (room.id && room.userId) {
               this.remove_room(room.id, room.userId);
           } else {
               console.error('Room ID or User ID is undefined.');
           }
        }
      });
  }

  remove_room(roomId: string, userId: string){
    this.roomService.remove_user_from_room(roomId,userId).subscribe({
      next: () => {
        this.rooms = this.rooms.filter(r => r.id !== roomId);
        this.snackBar.open('Room removed successfully.', 'Close', { duration: 3000 });
      },
      error: (err) => {
        console.error('Failed to remove room:', err);
        this.snackBar.open('Failed to remove room.', 'Close', { duration: 3000 });
      }
    })
  }
  // End of card 3

  // LOGIC OF THE CHART //

  chartData: ChartData<'line'> = {
    labels: [],
    datasets: []
  };

  chartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,

    plugins:{
      tooltip:{
        callbacks:{
          label:(context) => {
            const label = context.dataset.label || ``;
            const value = context.parsed.y;

            if(label.toLocaleLowerCase().includes('temperature')){
              return `${label}: ${value} °C`;
            } else if (label.toLowerCase().includes('humidity')) {
              return `${label}: ${value} %`;
            }else if (label.toLowerCase().includes('gas')) {
              return `${label}: ${value} – ${this.getGasLevelMessage(value)}`;
            }
            return `${label}: ${value}`;
          }
        }
      }
    }
  };

  select_room(room:Room){
    this.selectedRoom = room;
    this.selectedSensor = null;
    this.prepare_chart_data();
  }

  select_sensor(sensor: Sensor) {
    this.selectedSensor = sensor;
    this.prepare_chart_data();
}

  selected_date(date: Date){
    this.selectedDate = date;
    this.prepare_chart_data();
  }

  get_random_color(): string {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  }

  updateSensorDetailsInRooms(sensorId: string, details: Details[]) {
    const roomIndex = this.rooms.findIndex(room => {
      return room.sensors && room.sensors.some(sensor => sensor.id === sensorId);
    });

    if(roomIndex !== -1) {
      const sensorIndex = this.rooms[roomIndex].sensors?.findIndex(sensor => sensor.id === sensorId);

      if(sensorIndex !== -1) {
        this.rooms[roomIndex].sensors![sensorIndex!].details = [...details];
        this.roomService.setRooms([...this.rooms]);
      }
    }
  }

  prepare_chart_data(){
    if(!this.selectedSensor?.id) return;

    const selectedId = this.selectedSensor.id;
    const selectedDay = this.selectedDate.toDateString();

    console.log("Fetching data for: ", selectedDay);

    if(this.sensorSub){
      this.sensorSub.unsubscribe();
    }

    // Fetch for ngOnInit() //
    this.sensorService.get_sensor_data_by_date(selectedId, selectedDay).subscribe({
      next: (details: Details[]) => {
        this.process_chart_data(details);
        this.updateSensorDetailsInRooms(selectedId, details);
        console.log("Details of the selected sensor: ", details);
      },
      error: (err) => {
        console.error("Error fetching data:", err);
        this.reset_chart();
      }
    });

    this.sensorSub = interval(5000).pipe(
      switchMap(() => this.sensorService.get_sensor_data_by_date(selectedId, selectedDay))).subscribe({
       next: (details: Details[]) => {
         this.process_chart_data(details);
         this.updateSensorDetailsInRooms(selectedId, details);
         console.log("Details of the selected sensor: ", details);
       },
       error: (err) => {
         console.error("Error fetching data:", err);
         this.reset_chart();
       }
    });
 }

 process_chart_data(details: Details[]){
  const validDetails = details
        .filter(d => d.timestamp && d.data)
        .sort((a, b) => (a.timestamp!.seconds - b.timestamp!.seconds));

    if (validDetails.length === 0) {
        this.reset_chart();
        return;
    }

    const measurementTypes = Object.keys(validDetails[0].data!);

    this.chartData = {
        labels: validDetails.map(d => this.format_time(d)),
        datasets: measurementTypes.map(type => ({
            label: this.capitalize_first_letter(type),
            data: validDetails.map(d => d.data![type]),
            borderColor: this.get_random_color(),
            backgroundColor: 'rgba(0, 0, 0, 0.1)',
            tension: 0.4,
            pointRadius: 5,
            pointHoverRadius: 8
        }))
    };
 }

 format_time(detail: Details){
  let date: Date | null = null;

    if (typeof detail.get_timestamp_date === 'function') {
        date = detail.get_timestamp_date();
    } else if (detail.timestamp) {
        date = new Date(detail.timestamp.seconds * 1000);
    }

    return date ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';
 }

 capitalize_first_letter(str: string){
  return str.charAt(0).toUpperCase() + str.slice(1);
 }

 reset_chart(){
  this.hasDataForChart = false;
  this.chartData = { labels: [], datasets: [] };
 }
  // END OF LOGIG OF THE CHART //
}
