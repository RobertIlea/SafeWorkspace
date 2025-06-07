/**
 * Alert Model
 * This model represents an alert in the system.
 */
export class Alert{
  /**
   * The unique identifier for the alert.
   */
  alertId: string | undefined;

  /**
   * The unique identifier for the room where the alert was triggered.
   */
  roomId: string | undefined;

  /**
   * The unique identifier for the sensor that triggered the alert.
   */
  sensorId: string | undefined;

  /**
   * The timestamp when the alert was triggered.
   */
  timestamp: { seconds: number } | undefined;

  /**
   * The type of sensor that triggered the alert.
   */
  sensorType: string | undefined;

  /**
   * The data associated with the alert, such as sensor readings.
   */
  data: {[key: string]:number} | undefined;

  /**
   * A message describing the alert.
   */
  message: string | undefined;

  /**
   * Constructor for the Alert class.
   * @param alertId The id of the alert.
   * @param roomId The id of the room where the alert was triggered.
   * @param sensorId The id of the sensor that triggered the alert.
   * @param timestamp The timestamp when the alert was triggered.
   * @param sensorType The type of sensor that triggered the alert.
   * @param data The data associated with the alert, such as sensor readings.
   * @param message A message describing the alert.
   */
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
