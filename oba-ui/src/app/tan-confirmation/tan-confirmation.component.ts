import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AisService } from '../common/services/ais.service';
import { ShareDataService } from '../common/services/share-data.service';
import { ConsentAuthorizeResponse } from 'api/models';
import { RoutingPath } from '../common/models/routing-path.model';
import { ObaUtils } from '../common/utils/oba-utils';

@Component({
    selector: 'app-tan-confirmation',
    templateUrl: './tan-confirmation.component.html',
    styleUrls: ['./tan-confirmation.component.scss']
})
export class TanConfirmationComponent implements OnInit {

    private subscriptions: Subscription[] = [];
    public authResponse: ConsentAuthorizeResponse;
    public tanForm: FormGroup;
    public invalidTanCount = 0;

    constructor(private formBuilder: FormBuilder,
                private router: Router,
                private route: ActivatedRoute,
                private aisService: AisService,
                private shareService: ShareDataService) {
        this.tanForm = this.formBuilder.group({
            authCode: ['', Validators.required]
        });
    }

    public ngOnInit(): void {
        // fetch data that we save before after login
        this.shareService.currentData.subscribe(data => {
            if (data) {
                console.log('response object: ', data);
                this.shareService.currentData.subscribe(authResponse => this.authResponse = authResponse);
            }
        });
    }

    public onSubmit(): void {
        if (!this.authResponse) {
            console.log('Missing application data');
            return;
        }
        console.log('TAN: ' + this.tanForm.value);
        this.subscriptions.push(
            this.aisService.authrizedConsent({
                ...this.tanForm.value,
                encryptedConsentId: this.authResponse.encryptedConsentId,
                authorisationId: this.authResponse.authorisationId,
            }).subscribe(authResponse => {
                this.authResponse = authResponse;
                this.shareService.changeData(this.authResponse);
                this.router.navigate([`${RoutingPath.RESULT}`],
                    ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
            }, (error) => {
                this.invalidTanCount++;

                if (this.invalidTanCount >= 3) {
                  throw error;
                }
            })
        );
    }

    public cancel(): void {
      this.aisService.revokeConsent({
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId}).subscribe(authResponse => {
        console.log(authResponse);
        this.authResponse = authResponse;
        this.shareService.changeData(this.authResponse);
        this.router.navigate([`${RoutingPath.RESULT}`],
          ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
      });
    }

}
