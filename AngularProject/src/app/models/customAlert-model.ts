/**
 * CustomAlert model
 * This model represents a custom alert configuration for a user.
 */
export class CustomAlert{
  /**
   * Unique identifier for the custom alert
   */
  id: string | undefined;

  /**
   * User ID associated with the custom alert
   */
  userId: string | undefined;

  /**
   * Room ID where the alert is configured
   */
  roomId: string | undefined;

  /**
   * Sensor ID that the alert is monitoring
   */
  sensorId: string | undefined;

  /**
   * Type of sensor that the alert is monitoring
   */
  sensorType: string | undefined;

  /**
   * Parameter of the sensor that the alert is monitoring
   */
  parameter: string | undefined;

  /**
   * Condition that triggers the alert
   */
  condition: string | undefined;

  /**
   * Threshold value that defines the condition for the alert
   */
  threshold: number | undefined;

  /**
   * Message to be sent when the alert is triggered
   */
  message: string | undefined;

  /**
   * Constructor for the CustomAlert model
   * @param userId The ID of the user who owns the alert
   * @param roomId The ID of the room where the alert is set
   * @param sensorId The ID of the sensor being monitored
   * @param sensorType The type of sensor being monitored
   * @param parameter The parameter of the sensor being monitored
   * @param condition The condition that triggers the alert
   * @param threshold The threshold value for the alert condition
   * @param message The message to be sent when the alert is triggered
   */
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
