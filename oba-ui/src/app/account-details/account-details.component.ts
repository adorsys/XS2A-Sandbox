import {Component, OnInit} from '@angular/core';

@Component({
    selector: 'app-account-details',
    templateUrl: './account-details.component.html',
    styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit {

    public AccountDetails = [
        {bank: 'Deutsche Bank', type: 'Daily Account', iban: 'DE89 3704 0440 5320 1300 00'},
        {bank: 'Commerzbank', type: 'Saving Account', iban: 'DE89 0440 5320 3704 1300 00'},
        {bank: 'Sparda Bank', type: 'Checking Account', iban: 'DE89 5320 1300 3704 0440 00'}
    ];

    constructor() {
    }

    ngOnInit() {
    }

}
