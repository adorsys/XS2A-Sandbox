import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';

import {AUTH_RESPONSE, URL_PARAMS_PROVIDER} from '../common/constants/constants';
import {RoutingPath} from '../common/models/routing-path.model';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {ObaUtils} from '../common/utils/oba-utils';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

    private subscriptions: Subscription[] = [];
    private readonly authorisationId: string;
    private readonly encryptedConsentId: string;
    private readonly redirectId: string;
    loginForm: FormGroup;
    invalidCredentials: boolean;

    constructor(private formBuilder: FormBuilder,
                private router: Router,
                private route: ActivatedRoute,
                private aisService: AisService,
                private shareService: ShareDataService,
                @Inject(URL_PARAMS_PROVIDER) params) {
        this.encryptedConsentId = params.encryptedConsentId;
        this.authorisationId = params.redirectId;
        this.redirectId = params.redirectId;
    }

    public ngOnInit(): void {
        this.loginForm = this.formBuilder.group({
          login: ['', Validators.required],
          pin: ['', Validators.required]
        });

        // get ais auth code
        this.subscriptions.push(
            this.aisService.aisAuthCode({encryptedConsentId: this.encryptedConsentId, redirectId: this.redirectId})
              .subscribe(authCodeResponse => this.shareService.changeData(authCodeResponse),
              (error) => {
              console.log(error);
            })
        );
    }

    public onSubmit(): void {
        if (!!this.encryptedConsentId) {
            this.subscriptions.push(
                this.aisService.aisAuthorise({
                    ...this.loginForm.value,
                    encryptedConsentId: this.encryptedConsentId,
                    authorisationId: this.authorisationId,
                }).subscribe(authorisationResponse => {
                    this.shareService.changeData(authorisationResponse);
                    this.router.navigate([`${RoutingPath.BANK_OFFERED}`],
                        ObaUtils.getQueryParams(this.encryptedConsentId, this.authorisationId));
                }, (error) => {
                  console.log(error);
                  this.invalidCredentials = true;
                })
            );
        }
    }

    public ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

}
