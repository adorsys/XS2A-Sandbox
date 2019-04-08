import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {ConsentAuthorizeResponse, PaymentAuthorizeResponse} from '../../api/models';

@Injectable({
    providedIn: 'root'
})
export class ShareDataService {
    // response data
    private data = new BehaviorSubject<ConsentAuthorizeResponse|PaymentAuthorizeResponse>(null);
    currentData = this.data.asObservable();

    // operation type
    private operationType = new BehaviorSubject<string>(null);
    currentOperation = this.operationType.asObservable();

    constructor() {}

    changeData(data: ConsentAuthorizeResponse) {
        this.data.next(data);
    }

    setOperationType(operation: string) {
      this.operationType.next(operation);
    }
}
