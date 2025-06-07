/**
 * Sensor Model
 */
import { Details } from "./details-model";

/**
 * This model represents a sensor in the application.
 */
export class Sensor{
  /**
   * Unique identifier for the sensor
   */
  id: string | undefined;

  /**
   * Type of the sensor
   */
  sensorType: string | undefined;

  /**
   * Port number where the sensor is connected
   */
  port: number | undefined;

  /**
   * Details about the sensor
   */
  details: Details[] | undefined;

  /**
   * Indicates whether the sensor is currently active
   */
  active: boolean | undefined;

  /**
   * Constructor to initialize the Sensor model
   * @param id - Unique identifier for the sensor
   * @param sensorType - Type of the sensor
   * @param port - Port number where the sensor is connected
   * @param details - Details about the sensor
   * @param active - Indicates whether the sensor is currently active
   */
  constructor(id: string, sensorType: string, port: number,details: Details[], active: boolean = false) {
    this.id = id;
    this.sensorType = sensorType;
    this.port = port;
    this.details = details;
    this.active = active;
  }

  /**
   * Returns a string representation of the Sensor object
   */
  toString():string{
    return JSON.stringify(this);
  }
}

