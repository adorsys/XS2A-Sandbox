import { Component, OnInit } from '@angular/core';

import { ObaAisConsent } from '../../api/models/oba-ais-consent';
import { InfoService } from '../../common/info/info.service';
import { OnlineBankingService } from '../../common/services/online-banking.service';

@Component({
    selector: 'app-consents',
    templateUrl: './consents.component.html',
    styleUrls: ['./consents.component.scss']
})
export class ConsentsComponent implements OnInit {

    consents: ObaAisConsent[] = [];

    constructor(
        private onlineBankingService: OnlineBankingService,
        private infoService: InfoService) {
    }

    ngOnInit() {
        this.getConsents();
    }

    getConsents() {
        this.onlineBankingService.getConsents().subscribe(consents => {
            this.consents = consents;
        });
    }

    isConsentEnabled(consent: ObaAisConsent) {
        return consent.aisAccountConsent.consentStatus === 'VALID' || consent.aisAccountConsent.consentStatus === 'RECEIVED';
    }

    copiedConsentSuccessful() {
      this.infoService.openFeedback('copied encrypted consent to clipboard', { severity: 'info' });
    }

    revokeConsent(consent: ObaAisConsent) {
        if (!this.isConsentEnabled(consent)) {
          return false;
        }
        this.onlineBankingService.revokeConsent(consent.aisAccountConsent.id).subscribe(isSuccess => {
            if (isSuccess) {
                this.getConsents();
            } else {
                this.infoService.openFeedback('could not revoke the consent', { severity: 'error' });
            }
        });
    }

}
