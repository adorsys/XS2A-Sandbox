import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import {
    OnlineBankingOauthAuthorizationService, PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService
} from '../../api/services';
import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import LoginUsingPOSTParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.LoginUsingPOSTParams;
import AisAuthUsingGETParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AisAuthUsingGETParams;


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
    loginForm: FormGroup;
    errorMessage: string;
    invalidCredentials: boolean;

    private encryptedConsentId: string;
    private redirectId: string;
    private subscriptions: Subscription[] = [];

    constructor(public customizeService: CustomizeService,
                private formBuilder: FormBuilder,
                private router: Router,
                private infoService: InfoService,
                private activatedRoute: ActivatedRoute,
                private shareService: ShareDataService,
                private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService,
                private aisService: AisService,
                private PSUAISService: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService) {
    }

    ngOnInit() {
        this.initLoginForm();

        this.getAisAuthCode();
    }

    public onSubmit(): void {
        this.aisAuthorise({
            pin: this.loginForm.get('pin').value,
            login: this.loginForm.get('login').value,
            encryptedConsentId: this.encryptedConsentId,
            authorisationId: this.redirectId,
        });
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    private aisAuthorise(params: LoginUsingPOSTParams) {
        this.subscriptions.push(
            this.aisService.aisAuthorise(params).subscribe(authorisationResponse => {
                this.shareService.changeData(authorisationResponse);
                this.router.navigate([`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.GRANT_CONSENT}`]);
            }, (error: HttpErrorResponse) => {
                if (this.encryptedConsentId === undefined || this.redirectId === undefined) {
                    this.infoService.openFeedback('Consent data is missing. Please create consent prior to login', {
                        severity: 'error'
                    });
                } else {
                    if (error.status === 401) {
                        this.errorMessage = 'Invalid credentials';
                    } else if (error.status === 408) {
                      this.errorMessage = 'Consent is cancelled and cannot be authorized';
                    } else {
                        this.errorMessage = error.error ? error.error.message : error.message;
                    }
                }
            })
        );
    }

    private getAisAuthCode(): void {
        this.activatedRoute.queryParams.subscribe(params => {
            this.encryptedConsentId = params.encryptedConsentId;
            this.redirectId = params.redirectId;
            // set oauth2 param in shared service
            params.oauth2 ? this.shareService.setOauthParam(true) : this.shareService.setOauthParam(false);
            const aisAuthCodeParams: AisAuthUsingGETParams  = {
                encryptedConsentId: this.encryptedConsentId,
                redirectId: this.redirectId,
                ...params.token && { Authorization: 'Bearer ' + params.token },
            };


            this.subscriptions.push(
                this.aisService.aisAuthCode(aisAuthCodeParams)
                    .subscribe(authCodeResponse => {
                            this.shareService.changeData(authCodeResponse.body);
                            if (authCodeResponse.headers.get('Authorization')) {
                                this.aisAuthorise({
                                    encryptedConsentId: this.encryptedConsentId,
                                    authorisationId: this.redirectId
                                });
                            }
                        },
                        (error) => {
                            console.log(error);
                        })
            );
        });
    }

    private initLoginForm(): void {
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required],
            pin: ['', Validators.required]
        });
    }

}
