import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Subscription } from 'rxjs';
import { ConsentAuthorizeResponse } from 'api/models';
import { ShareDataService } from '../common/services/share-data.service';
import { AisService } from '../common/services/ais.service';

@Component({
    selector: 'app-result-page',
    templateUrl: './result-page.component.html',
    styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {

    private subscriptions: Subscription[] = [];
    public authResponse: ConsentAuthorizeResponse;

    constructor(public router: Router,
                private route: ActivatedRoute,
                private aisService: AisService,
                private shareService: ShareDataService) {
        // this.route.params.subscribe(response => {
        //     this.show = (response.id == 1);
        // });
    }

    public ngOnInit(): void {
        this.shareService.currentData.subscribe(data => {
            if (data) {
                this.shareService.currentData.subscribe(authResponse => this.authResponse = authResponse);
            }
        });
    }

    public backToTpp(): void {
        this.aisDone();
    }
    public forgetConsent(): void {
        this.aisDone();
    }

    private aisDone(): void {
        this.aisService.aisDone({
            encryptedConsentId: this.authResponse.encryptedConsentId,
            authorisationId: this.authResponse.authorisationId,
            forgetConsent: 'true',
            backToTpp: 'true'
        });
    }

    public scaStatus(status: string): boolean {
        return this.authResponse && this.authResponse.scaStatus === status;
    }

}
