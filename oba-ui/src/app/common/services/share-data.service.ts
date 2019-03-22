import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {ConsentAuthorizeResponse} from "../../../../api/models";

@Injectable({
    providedIn: 'root'
})
export class ShareDataService {

    // response object
    private consentAuthorizeResponse = new BehaviorSubject(null);
    private currentConsentAuthorizeResponse = this.consentAuthorizeResponse.asObservable();

    // accounts array
    private accounts = new BehaviorSubject(null);
    private selectedAccounts = this.accounts.asObservable();

    constructor() {}

    public setConsentAuthorizeResponse(scaMethods: ConsentAuthorizeResponse): void {
        this.consentAuthorizeResponse.next(scaMethods)
    }

    public getCurrentConsentAuthorizeResponse(): Observable<ConsentAuthorizeResponse> {
      return this.currentConsentAuthorizeResponse;
    }

    // TODO: to be refactored when we know what object we receive in accounts
    public setSelectedAccounts(accounts: any): void {
      this.accounts.next(accounts)
    }

    // TODO: to be refactored when we know what object we receive in accounts
    public getCurrentlySelectedAccounts(): Observable<any> {
      return this.selectedAccounts;
    }

}
