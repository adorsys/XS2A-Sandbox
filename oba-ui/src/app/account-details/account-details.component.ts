import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';

import {ShareDataService} from '../common/services/share-data.service';
import { ConsentAuthorizeResponse } from 'api/models';

@Component({
    selector: 'app-account-details',
    templateUrl: './account-details.component.html',
    styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit, OnDestroy {

    private subscriptions: Subscription[] = [];
    // public accountDetails: any = {};

    public authResponse: ConsentAuthorizeResponse;

    constructor(private sharedService: ShareDataService) {}

    ngOnInit() {
        this.sharedService.currentData.subscribe(
            authResponse => this.authResponse = authResponse
        );
    }

    public ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    get accounts(): string[] {
        if (!this.authResponse) {return []; }
        return this.authResponse.consent.access.accounts || [];
    }
    get balances(): string[] {
        if (!this.authResponse) {return []; }
        return this.authResponse.consent.access.balances || [];
    }
    get transactions(): string[] {
        if (!this.authResponse) {return []; }
        return this.authResponse.consent.access.transactions || [];
    }

}
