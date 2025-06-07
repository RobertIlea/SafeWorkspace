/**
 * Details model for storing timestamp and data.
 */
export class Details{
  /**
   * Timestamp in seconds and optional nanoseconds.
   */
  timestamp: { seconds: number, nanos?: number } | undefined;

  /**
   * Data as a key-value pair where keys are strings and values are numbers.
   */
  data: {[key: string]:number} | undefined;

  /**
   * Constructor to initialize the Details object.
   * @param timestamp - An object containing seconds and optional nanoseconds.
   * @param data - An object containing key-value pairs where keys are strings and values are numbers.
   */
  constructor(timestamp: { seconds: number, nanos?: number }, data: {[key: string]:number} ){
      this.timestamp = timestamp;
      this.data = data;
  }

  /**
   * Returns the timestamp as a Date object.
   */
  get_timestamp_date(): Date | null {
        if(!this.timestamp) return null;
        return new Date(this.timestamp.seconds * 1000 + (this.timestamp.nanos || 0) / 1000000);
    }
}
