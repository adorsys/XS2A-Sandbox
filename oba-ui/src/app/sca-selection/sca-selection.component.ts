import { Component, OnInit } from '@angular/core';
import { ScaUserDataTO } from '../../../api/models/sca-user-data-to';
import { Router } from '@angular/router';
import { ShareDataService } from '../common/services/share-data.service';
import { ConsentAuthorizeResponse } from 'api/models';
import { AisService } from '../common/services/ais.service';
import { Subscription } from 'rxjs';
import { RoutingPath } from '../common/models/routing-path.model';
import { ObaUtils } from '../common/utils/oba-utils';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
    selector: 'app-sca-selection',
    templateUrl: './sca-selection.component.html',
    styleUrls: ['./sca-selection.component.scss']
})
export class ScaSelectionComponent implements OnInit {

    private subscriptions: Subscription[] = [];
    public authResponse: ConsentAuthorizeResponse;
    public selectedScaMethod: ScaUserDataTO;
    public scaForm: FormGroup;

    sorry: 0;
    success: 1;
    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private aisService: AisService,
        private shareService: ShareDataService) {
        this.scaForm = this.formBuilder.group({
            scaMethod: ['', Validators.required],
        });

    }

    ngOnInit() {
        // fetch data that we save before after login
        this.shareService.currentData.subscribe(data => {
            if (data) {
                // TODO extract the Accounts, Balances and Transactions from data.value
                console.log('response object: ', data);
                this.shareService.currentData.subscribe(authResponse => {
                    this.authResponse = authResponse;
                    if (this.authResponse.scaMethods && this.authResponse.scaMethods.length === 1) {
                        this.selectedScaMethod = this.authResponse.scaMethods[0];
                    }
                });
            }
        });
    }

    public onSubmit(): void {
        console.log('selecting sca');
        if (!this.authResponse || !this.selectedScaMethod) {
            console.log('No sca method selected.');
            return;
        }
        this.subscriptions.push(
            this.aisService.selectScaMethod({
                encryptedConsentId: this.authResponse.encryptedConsentId,
                authorisationId: this.authResponse.authorisationId,
                scaMethodId: this.selectedScaMethod.id
            }).subscribe(authResponse => {
                this.authResponse = authResponse;
                this.shareService.changeData(this.authResponse);
                this.router.navigate([`${RoutingPath.TAN_CONFIRMATION}`],
                    ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
            }, errors => console.log('http error please catch me!'))
        );
    }

    public onCancel(): void {
        this.router.navigate(['/result', this.sorry]);
    }

    get scaMehods(): ScaUserDataTO[] {
        return this.authResponse ? this.authResponse.scaMethods : [];
    }

    handleMethodSelectedEvent(scaMethod: ScaUserDataTO): void {
        console.log('No sca method selected.');
        this.selectedScaMethod = scaMethod;
    }
}
