import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {ConsentAuthorizeResponse} from '../../api/models';

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
