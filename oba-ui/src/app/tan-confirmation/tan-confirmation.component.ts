import {Component, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Router, ActivatedRoute} from '@angular/router';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {ConsentAuthorizeResponse} from '../api/models';
import {RoutingPath} from '../common/models/routing-path.model';
import {ObaUtils} from '../common/utils/oba-utils';
import {PisService} from "../common/services/pis.service";

@Component({
  selector: 'app-tan-confirmation',
  templateUrl: './tan-confirmation.component.html',
  styleUrls: ['./tan-confirmation.component.scss']
})
export class TanConfirmationComponent implements OnInit {

  public authResponse: ConsentAuthorizeResponse;
  public tanForm: FormGroup;
  public invalidTanCount = 0;

  private subscriptions: Subscription[] = [];
  private operation: string;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private pisService: PisService,
              private shareService: ShareDataService) {
    this.tanForm = this.formBuilder.group({
      authCode: ['', Validators.required]
    });
  }

  public ngOnInit(): void {
    // get current operation
    this.shareService.currentOperation
      .subscribe((operation: string) => {
        this.operation = operation;
      });

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

    if (this.operation == 'ais') {
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
    } else if (this.operation == 'pis') {
      this.subscriptions.push(
        this.pisService.authorizePayment({
          ...this.tanForm.value,
          encryptedPaymentId: this.authResponse.encryptedConsentId,
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
  }

  public cancel(): void {
    this.aisService.revokeConsent({
      encryptedConsentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId
    }).subscribe(authResponse => {
      console.log(authResponse);
      this.authResponse = authResponse;
      this.shareService.changeData(this.authResponse);
      this.router.navigate([`${RoutingPath.RESULT}`],
        ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
    });
  }

}
