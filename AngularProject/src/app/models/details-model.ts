export class Details{
    timestamp: { seconds: number, nanos?: number } | undefined;
    data: {[key: string]:number} | undefined;

    constructor(timestamp: { seconds: number, nanos?: number }, data: {[key: string]:number} ){
        this.timestamp = timestamp;
        this.data = data;
    }
}