import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';

import {URL_PARAMS_PROVIDER} from '../common/constants/constants';
import {RoutingPath} from '../common/models/routing-path.model';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {ObaUtils} from '../common/utils/oba-utils';
import {PisService} from "../common/services/pis.service";
import {PisCancellationService} from "../common/services/pis-cancellation.service";
import {PSUAISService} from "../api/services/psuais.service";
import LoginUsingPOSTParams = PSUAISService.LoginUsingPOSTParams;
import {PSUPISService} from "../api/services/psupis.service";
import LoginUsingPOST2Params = PSUPISService.LoginUsingPOST2Params;
import {PSUPISCancellationService} from "../api/services/psupiscancellation.service";
import LoginUsingPOST1Params = PSUPISCancellationService.LoginUsingPOST1Params;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  invalidCredentials: boolean;

  private subscriptions: Subscription[] = [];
  private readonly operation: string;
  private readonly paymentId: string;
  private readonly authorisationId: string;
  private readonly encryptedConsentId: string;
  private readonly redirectId: string;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private pisService: PisService,
              private pisCancellationService: PisCancellationService,
              private shareService: ShareDataService,
              @Inject(URL_PARAMS_PROVIDER) params) {
    this.operation = params.operation;
    this.encryptedConsentId = params.encryptedConsentId;
    this.paymentId = params.paymentId;
    this.authorisationId = params.redirectId;
    this.redirectId = params.redirectId;
  }

  ngOnInit() {
    this.initAppData();
    this.initLoginForm();

    // get auth code and cookies
    if (this.operation === 'ais') {
      this.subscriptions.push(
        this.aisService.aisAuthCode({encryptedConsentId: this.encryptedConsentId, redirectId: this.redirectId})
          .subscribe(authCodeResponse => this.shareService.changeData(authCodeResponse),
            (error) => {
              console.log(error);
            })
      );
    } else if (this.operation === 'pis' || this.operation === 'pis-cancellation') {
      this.subscriptions.push(
        this.pisService.pisAuthCode({encryptedPaymentId: this.paymentId, redirectId: this.redirectId})
          .subscribe(authCodeResponse => this.shareService.changeData(authCodeResponse),
            (error) => {
              console.log(error);
            })
      );
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onSubmit(): void {

    if (this.operation === 'ais') {
      this.subscriptions.push(
        this.aisService.aisAuthorise(<LoginUsingPOSTParams>{
          ...this.loginForm.value,
          encryptedConsentId: this.encryptedConsentId,
          authorisationId: this.authorisationId,
        }).subscribe(authorisationResponse => {
          this.shareService.changeData(authorisationResponse);
          this.router.navigate([`${RoutingPath.BANK_OFFERED}`]);
        }, (error) => {
          console.log(error);
          this.invalidCredentials = true;
        })
      );
    } else if (this.operation === 'pis') {
      this.subscriptions.push(
        this.pisService.pisLogin(<LoginUsingPOST2Params>{
          ...this.loginForm.value,
          encryptedPaymentId: this.paymentId,
          authorisationId: this.authorisationId,
        }).subscribe(authorisationResponse => {
          console.log(authorisationResponse);
          this.shareService.changeData(authorisationResponse);
          this.router.navigate([`${RoutingPath.SELECT_SCA}`]);
        }, (error) => {
          console.log(error);
          this.invalidCredentials = true;
        })
      )
    } else if (this.operation === 'pis-cancellation') {
      this.subscriptions.push(
        this.pisCancellationService.pisCancellationLogin(<LoginUsingPOST1Params>{
          ...this.loginForm.value,
          encryptedPaymentId: this.paymentId,
          authorisationId: this.authorisationId,
        }).subscribe(authorisationResponse => {
          console.log(authorisationResponse);
          this.shareService.changeData(authorisationResponse);
          this.router.navigate([`${RoutingPath.SELECT_SCA}`]);
        }, (error) => {
          console.log(error);
          this.invalidCredentials = true;
        })
      );
    }
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }

  private initAppData(): void {
    this.shareService.setOperationType(this.operation);
    this.shareService.setEncryptedConsentId(this.encryptedConsentId);
    this.shareService.setPaymentId(this.paymentId);
    this.shareService.setAuthorisationId(this.authorisationId);
  }

}
