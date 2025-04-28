import { Details } from "./details-model";

export class Sensor{
    id: string | undefined;
    sensorType: string | undefined;
    port: number | undefined;
    details: Details[] | undefined;
    

constructor(id: string, sensorType: string, port: number,details: Details[]){
        this.id = id;
        this.sensorType = sensorType;
        this.port = port;
        this.details = details;
    }

 toString():string{
    return JSON.stringify(this);
 }
}

