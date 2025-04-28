import { NativeDateModule } from "@angular/material/core";
import { Sensor } from "./sensor-model";

export class Room{
    id: string | undefined;
    sensors: Sensor[] | undefined;
    name: string | undefined;

    constructor(id: string,name: string,sensors: Sensor[]){
        this.id = id;
        this.name = name;
        this.sensors = sensors;
    }
}