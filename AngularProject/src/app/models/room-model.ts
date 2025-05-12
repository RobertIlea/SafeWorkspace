import { Sensor } from "./sensor-model";

export class Room{
    id?: string ;
    name: string | undefined;
    sensors: Sensor[] | undefined;
    userId: string | undefined;

    constructor(id: string | undefined,sensors: Sensor[],name:string, userId: string){
        this.id = id;
        this.sensors = sensors;
        this.name = name;
        this.userId = userId;
    }
}
