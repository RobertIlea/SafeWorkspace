/**
 * Room Model
 */
import { Sensor } from "./sensor-model";

/**
 * Represents a room in the system.
 */
export class Room{

  /**
   * Unique identifier for the room.
   */
  id?: string ;
  /**
   * Name of the room.
   */
  name: string | undefined;

  /**
   * List of sensors associated with the room.
   */
  sensors: Sensor[] | undefined;

  /**
   * Unique identifier for the user who owns the room.
   */
  userId: string | undefined;

  /**
   * Constructor for the Room class.
   * @param id - Unique identifier for the room.
   * @param sensors - List of sensors associated with the room.
   * @param name - Name of the room.
   * @param userId - Unique identifier for the user who owns the room.
   */
  constructor(id: string | undefined,sensors: Sensor[],name:string, userId: string){
      this.id = id;
      this.sensors = sensors;
      this.name = name;
      this.userId = userId;
  }
}
