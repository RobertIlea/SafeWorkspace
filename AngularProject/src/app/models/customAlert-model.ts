export class CustomAlert{
  id: string | undefined;
  userId: string | undefined;
  roomId: string | undefined;
  sensorId: string | undefined;
  sensorType: string | undefined;
  parameter: string | undefined;
  condition: string | undefined;
  threshold: number | undefined;
  message: string | undefined;

  constructor(userId: string, roomId: string, sensorId: string, sensorType: string, parameter: string, condition: string, threshold: number, message: string){
    this.userId = userId;
    this.roomId = roomId;
    this.sensorId = sensorId;
    this.sensorType = sensorType;
    this.parameter = parameter;
    this.condition = condition;
    this.threshold = threshold;
    this.message = message;
  }
}
