import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {ConsentAuthorizeResponse} from 'api/models';

/**
 * @author Gerard Talla
 * Use this service for sharing data between components.
 * Note: For best usage, you should pass your data by giving a key that will be use to retrieve your expected data.
 */
@Injectable({
    providedIn: 'root'
})
export class ShareDataService {
    private data = new BehaviorSubject<ConsentAuthorizeResponse>(null);
    currentData = this.data.asObservable();


    constructor() {
    }

    changeData(data: ConsentAuthorizeResponse) {
        this.data.next(data);
    }
}
