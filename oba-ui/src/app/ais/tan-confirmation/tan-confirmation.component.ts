import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';

import AuthrizedConsentUsingPOSTParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AuthrizedConsentUsingPOSTParams;
import { PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService } from '../../api/services/psuaisprovides-access-to-online-banking-account-functionality.service';
@Component({
  selector: 'app-tan-confirmation',
  templateUrl: './tan-confirmation.component.html',
  styleUrls: ['./tan-confirmation.component.scss'],
})
export class TanConfirmationComponent implements OnInit, OnDestroy {
  public authResponse: ConsentAuthorizeResponse;
  public tanForm: FormGroup;
  public invalidTanCount = 0;

  private subscriptions: Subscription[] = [];
  private operation: string;
  private oauth2Param: boolean;

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private shareService: ShareDataService
  ) {}

  public ngOnInit(): void {
    this.initTanForm();

    // get query params
    this.shareService.currentOperation.subscribe((operation: string) => {
      this.operation = operation;
    });

    // fetch data that we save before after login
    this.shareService.currentData.subscribe((data) => {
      if (data) {
        console.log('response object: ', data);
        this.shareService.currentData.subscribe(
          (authResponse) => (this.authResponse = authResponse)
        );
      }
    });

    // fetch oauth param value
    this.shareService.oauthParam.subscribe((oauth2: boolean) => {
      this.oauth2Param = oauth2;
    });
  }

  ngOnDestroy() {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  public onSubmit(): void {
    if (!this.authResponse) {
      return;
    }
    console.log('TAN: ' + this.tanForm.value);

    this.subscriptions.push(
      this.aisService
        .authrizedConsent({
          ...this.tanForm.value,
          encryptedConsentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
        } as AuthrizedConsentUsingPOSTParams)
        .subscribe(
          (authResponse) => {
            console.log(authResponse);
            this.router
              .navigate(
                [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
                {
                  queryParams: {
                    encryptedConsentId: this.authResponse.encryptedConsentId,
                    authorisationId: this.authResponse.authorisationId,
                    oauth2: this.oauth2Param,
                  },
                }
              )
              .then(() => {
                this.authResponse = authResponse;
                this.shareService.changeData(this.authResponse);
              });
          },
          (error) => {
            this.invalidTanCount++;

            if (this.invalidTanCount >= 3) {
              this.router
                .navigate(
                  [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
                  {
                    queryParams: {
                      encryptedConsentId: this.authResponse.encryptedConsentId,
                      authorisationId: this.authResponse.authorisationId,
                      oauth2: this.oauth2Param,
                    },
                  }
                )
                .then(() => {
                  throw error;
                });
            }
          }
        )
    );
  }

  public onCancel(): void {
    this.aisService
      .revokeConsent({
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
      })
      .subscribe((authResponse) => {
        console.log(authResponse);
        this.router
          .navigate(
            [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
            {
              queryParams: {
                encryptedConsentId: this.authResponse.encryptedConsentId,
                authorisationId: this.authResponse.authorisationId,
              },
            }
          )
          .then(() => {
            this.authResponse = authResponse;
            this.shareService.changeData(this.authResponse);
          });
      });
  }

  private initTanForm(): void {
    this.tanForm = this.formBuilder.group({
      authCode: ['', Validators.required],
    });
  }
}
