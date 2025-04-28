import { Component, inject, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/room-mopdel';
import { SensorService } from '../../services/sensor.service';
import { Sensor } from '../../models/sensor-model';
import { trigger, style, animate, transition } from '@angular/animations';
import { MatDialog } from '@angular/material/dialog';
import { RoomDialogComponent } from '../room-dialog/room-dialog.component';
import { withDebugTracing } from '@angular/router';
import { AddRoomComponent } from '../add-room/add-room.component';
import { ChartData, ChartOptions } from 'chart.js';

function parse_firestore_timestamp(timestamp: {seconds: number, nanos?: number}):Date{
  if(!timestamp || typeof timestamp.seconds !== 'number'){
    return new Date();
  }
  return new Date(timestamp.seconds * 1000);
}

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
export class DashboardComponent implements OnInit {
  rooms: Room[] = [];
  errorMessage: string = '';
  newRoomName: string = '';
  user: string = "4Uqgxsat3pZVz2XSksOg";
  showDialog: boolean = false; 
  sensors: Sensor[] = [];
  selectedRoom: Room | null = null;
  selectedSensor: Sensor | null = null;
  selectedDate: Date = new Date();
  hasDataForChart: boolean = true;

  constructor(
    private roomService: RoomService,
    private sensorService: SensorService,
    private dialog: MatDialog
  ){}

  ngOnInit(): void {
      this.get_rooms_by_user_id();
  }
  
  get_rooms_by_user_id(): void {
    this.roomService.get_rooms_by_user_id(this.user).subscribe({
      next: (room: Room[]) => {
        this.rooms = room;
        console.log("Rooms: " , this.rooms[0]);   
        
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

  open_room_dialog(room: any){
    this.dialog.open(RoomDialogComponent, {
      width: '600px',
      data: { room }
    });
  }

  get_sensors(){

  }

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

  select_sensor(sensor: Sensor){
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
  prepare_chart_data(){
    if(!this.selectedSensor) return;

    const selectedDay = this.selectedDate.toDateString();

    const filteredData = this.selectedSensor.details?.filter(detail =>{
      if(!detail.timestamp) return false;

      const dateObj = parse_firestore_timestamp(detail.timestamp);
      console.log("Data dupa parsare: " + dateObj);

      const detailDate = dateObj.toDateString();
      return detailDate === selectedDay;
    });

    this.hasDataForChart = (filteredData?.length ?? 0) > 0;

    if(!this.hasDataForChart){
      this.chartData = {labels:[],datasets:[]};
      return;
    }
    
    // Extract all the keys from data map
    const allKeys = Object.keys(filteredData?.[0]?.data?? {});
    
    // Dataset pentru fiecare cheie
    const datasets = allKeys.map(key => ({
      label: key.charAt(0).toUpperCase() + key.slice(1),
      data: (filteredData ?? []).map(d => d.data?.[key] ?? 0),
      fill: false,
      tension: 0.3,
      borderColor: this.get_random_color(),
      backgroundColor: this.get_random_color(),
      pointRadius: 5,
      pointHoverRadius: 7,
      showLine: true,
    }));

    this.chartData = {
      labels: (filteredData ?? []).map(d => {
        return parse_firestore_timestamp(d.timestamp!).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      }),
      datasets: datasets,
    }
  }

  // END OF LOGIG OF THE CHART //

}

