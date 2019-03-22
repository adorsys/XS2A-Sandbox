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

    public setSelectedAccounts(accounts: string[]): void {
      this.accounts.next(accounts)
    }

    public getCurrentlySelectedAccounts(): Observable<string[]> {
      return this.selectedAccounts;
    }

}
