export class Details{
    timestamp: { seconds: number, nanos?: number } | undefined;
    data: {[key: string]:number} | undefined;

    constructor(timestamp: { seconds: number, nanos?: number }, data: {[key: string]:number} ){
        this.timestamp = timestamp;
        this.data = data;
    }

    get_timestamp_date(): Date | null {
        if(!this.timestamp) return null;
        return new Date(this.timestamp.seconds * 1000 + (this.timestamp.nanos || 0) / 1000000);
    }
}