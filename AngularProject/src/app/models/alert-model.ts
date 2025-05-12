export class Alert{
    alertId: string | undefined;
    roomId: string | undefined;
    sensorId: string | undefined;
    timestamp: { seconds: number } | undefined;
    sensorType: string | undefined;
    data: {[key: string]:number} | undefined;
    message: string | undefined;

    constructor(alertId: string, roomId: string, sensorId: string, timestamp: { seconds: number },sensorType: string | undefined,data: {[key: string]:number} | undefined, message: string){
        this.alertId = alertId;
        this.roomId = roomId;
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.data = data;
        this.message = message;
    }
}
