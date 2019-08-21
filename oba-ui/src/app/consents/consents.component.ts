import {Component, OnInit} from '@angular/core';
import {AisAccountConsent} from '../api/models';
import {OnlineBankingService} from '../common/services/online-banking.service';
import {InfoService} from "../common/info/info.service";
import {map} from 'rxjs/operators';

@Component({
    selector: 'app-consents',
    templateUrl: './consents.component.html',
    styleUrls: ['./consents.component.scss']
})
export class ConsentsComponent implements OnInit {

    consents: AisAccountConsent[] = [];

    constructor(
        private onlineBankingService: OnlineBankingService,
        private infoService: InfoService) {
    }

    ngOnInit() {
        this.getConsents();
    }

    getConsents() {
        this.onlineBankingService.getConsents().pipe(
            map(consents => consents.map(consent => consent.aisAccountConsent))
        ).subscribe(consents => {
            this.consents = consents;
        });
    }

    isConsentEnabled(consent: AisAccountConsent) {
        return consent.consentStatus === 'VALID' || consent.consentStatus === 'RECEIVED';
    }

    revokeConsent(consent: AisAccountConsent) {
        if(!this.isConsentEnabled(consent)) return false;
        this.onlineBankingService.revokeConsent(consent.id).subscribe(isSuccess => {
            if(isSuccess) {
                this.getConsents();
            } else {
                this.infoService.openFeedback('could not revoke the consent', { severity: 'error' })
            }
        });
    }

}
