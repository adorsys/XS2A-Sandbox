import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {URL_PARAMS_PROVIDER} from '../../../common/constants/constants';
import {RoutingPath} from '../../../common/models/routing-path.model';
import {ShareDataService} from '../../../common/services/share-data.service';
import {ObaUtils} from '../../../common/utils/oba-utils';

@Component({
    selector: 'app-bank-offered',
    templateUrl: './bank-offered.component.html',
    styleUrls: ['./bank-offered.component.scss']
})
export class BankOfferedComponent implements OnInit {

    private authorisationId: string;
    private encryptedConsentId: string;
    public bankOfferedForm: FormGroup;
    public Accounts = [
        {
            bank: 'Commerzbank',
            iban: 'FR76 1234 5987 6501 2345 6789 04',
            bban: 'BARC12345612345678',
            pan: '5409050000000000',
            maskedPan: '123456xxxxxx1234',
            msisdn: '+49 170 1234567',
            currency: 'EUR'
        },
        {
            bank: 'Deutsche Bank',
            iban: 'DE76 1234 5987 6501 2345 6789 00',
            bban: 'BICC12345612345678',
            pan: '5409050000000000',
            maskedPan: '123456xxxxxx1234',
            msisdn: '+49 170 1234567',
            currency: 'EUR'
        },
        {
            bank: 'Sparda Bank',
            iban: 'ME76 1213 4343 9876 5012 3400 14',
            bban: 'BOQC12345612345678',
            pan: '0500000000056770',
            maskedPan: '123456xxxxxx1234',
            msisdn: '+49 170 1234567',
            currency: 'EUR'
        }
    ];

    constructor(@Inject(URL_PARAMS_PROVIDER) params,
                private formBuilder: FormBuilder,
                private router: Router,
                private _shareService: ShareDataService) {
        this.encryptedConsentId = params.encryptedConsentId;
        this.authorisationId = params.authorisationId;
        this.bankOfferedForm = this.formBuilder.group({
            accounts: ['', Validators.required]
        });
    }

    public ngOnInit(): void {
    }

    public onSubmit(): void {
        this._shareService.setSelectedAccounts(this.bankOfferedForm.value);
        console.log('bank value: ', this.bankOfferedForm.value);
        this.router.navigate([`${RoutingPath.SELECT_SCA}`],
            ObaUtils.getQueryParams(this.encryptedConsentId, this.authorisationId));
    }

    public cancel(): void {
        console.log('the button cancel is pressed');
    }

}
