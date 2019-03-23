import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {SELECTED_ACCOUNT} from '../common/constants/constants';

import {ShareDataService} from '../common/services/share-data.service';

@Component({
    selector: 'app-account-details',
    templateUrl: './account-details.component.html',
    styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit, OnDestroy {

    private subscriptions: Subscription[] = [];
    public accountDetails: any = {};
    /*[
        {bank: 'Deutsche Bank', type: 'Daily Account', iban: 'DE89 3704 0440 5320 1300 00'},
        {bank: 'Commerzbank', type: 'Saving Account', iban: 'DE89 0440 5320 3704 1300 00'},
        {bank: 'Sparda Bank', type: 'Checking Account', iban: 'DE89 5320 1300 3704 0440 00'}
    ];*/

    constructor(private _shareService: ShareDataService) {}

    public ngOnInit(): void {
        // fetch data that we save before after BankOffered
        this.subscriptions.push(
            this._shareService.currentData.subscribe(data => {
                if (data && data.key === SELECTED_ACCOUNT) {
                    this.accountDetails = data.value;
                    console.log('account info: ', this.accountDetails);
                }
            })
        );
    }

    public ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

}
