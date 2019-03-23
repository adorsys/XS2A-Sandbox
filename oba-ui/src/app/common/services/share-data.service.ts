import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

/**
 * @author Gerard Talla
 * Use this service for sharing data between components.
 * Note: For best usage, you should pass your data by giving a key that will be use to retrieve your expected data.
 */
@Injectable({
    providedIn: 'root'
})
export class ShareDataService {
    private data = new BehaviorSubject<KeyValue>(null);
    currentData = this.data.asObservable();

    constructor() {
    }

    changeData(data: KeyValue) {
        this.data.next(data);
    }
}

export interface KeyValue {
    key: string;
    value: any;
}


