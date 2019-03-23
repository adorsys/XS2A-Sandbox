import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AccountDetailsTO} from '../../../api/models/account-details-to';
import {LOGIN_RESPONSE, SELECTED_ACCOUNT, URL_PARAMS_PROVIDER} from '../common/constants/constants';
import {RoutingPath} from '../common/models/routing-path.model';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {ObaUtils} from '../common/utils/oba-utils';

@Component({
    selector: 'app-bank-offered',
    templateUrl: './bank-offered.component.html',
    styleUrls: ['./bank-offered.component.scss']
})
export class BankOfferedComponent implements OnInit {

    private subscriptions: Subscription[] = [];
    private authorisationId: string;
    private encryptedConsentId: string;
    public bankOfferedForm: FormGroup;
    public accounts: Array<AccountDetailsTO>;

    constructor(@Inject(URL_PARAMS_PROVIDER) params,
                private formBuilder: FormBuilder,
                private router: Router,
                private _shareService: ShareDataService,
                private _aisService: AisService) {
        this.encryptedConsentId = params.encryptedConsentId;
        this.authorisationId = params.authorisationId;
        this.bankOfferedForm = this.formBuilder.group({
            accounts: ['', Validators.required]
        });
    }

    public ngOnInit(): void {
        this.subscriptions.push(
          this._aisService.getAccountsList().subscribe(
              accounts => this.accounts = accounts,
              error => console.log(error)
          )
        );
        // fetch data that we save before after login
        /*this._shareService.currentData.subscribe(data => {
            if (data && data.key === LOGIN_RESPONSE) {
                // TODO extract the Accounts, Balances and Transactions from data.value
                console.log('response object: ', data.value);
            }
        });*/
    }

    public onSubmit(): void {
        this._shareService.changeData({
            key: SELECTED_ACCOUNT,
            value: this.bankOfferedForm.get('accounts').value
        });
        this.router.navigate([`${RoutingPath.SELECT_SCA}`],
            ObaUtils.getQueryParams(this.encryptedConsentId, this.authorisationId));
    }

    public cancel(): void {
        console.log('the button cancel is pressed');
    }

}
