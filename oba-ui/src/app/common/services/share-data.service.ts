import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ShareDataService {

    private date = {};

    constructor() {
    }

    public setResponse(option, value): void {
        this.date[option] = value;
    }

    public getResponse(): {} {
        return this.date;
    }
}
