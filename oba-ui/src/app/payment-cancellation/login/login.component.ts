import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { CustomizeService } from '../../common/services/customize.service';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';

import LoginUsingPOST2Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params;
import {PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService} from "../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string;
  invalidCredentials: boolean;

  private encryptedPaymentId: string;
  private redirectId: string;

  private subscriptions: Subscription[] = [];


  constructor(public customizeService: CustomizeService,
              private formBuilder: FormBuilder,
              private infoService: InfoService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private pisCancellationService: PisCancellationService,
              private shareService: ShareDataService,
              private pisService: PisService) {
  }

  ngOnInit() {
    this.initLoginForm();

    this.getPisAuthCode();
  }

  public onSubmit(): void {
    this.subscriptions.push(
      this.pisCancellationService.pisCancellationLogin({
        ...this.loginForm.value,
        encryptedPaymentId: this.encryptedPaymentId,
        authorisationId: this.redirectId,
      } as LoginUsingPOST2Params).subscribe(authorisationResponse => {
        console.log(authorisationResponse);
        this.shareService.changeData(authorisationResponse);
        this.router.navigate([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.CONFIRM_CANCELLATION}`]);
      }, (error: HttpErrorResponse) => {
        // if paymentId or redirectId is missing
        if (this.encryptedPaymentId === undefined || this.redirectId === undefined) {
          this.infoService.openFeedback('Payment data is missing. Please initiate payment cancellation prior to login', {
            severity: 'error'
          });
        } else {
          if (error.status === 401) {
            this.errorMessage = 'Invalid credentials';
          } else {
            this.errorMessage = error.error ? error.error.message : error.message;
          }
          throw new HttpErrorResponse(error);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private getPisAuthCode(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.encryptedPaymentId = params.paymentId;
      this.redirectId = params.redirectId;

      // set oauth2 param in shared service
      params.oauth2 ? this.shareService.setOauthParam(true) : this.shareService.setOauthParam(false);

      this.subscriptions.push(
        this.pisService.pisAuthCode({encryptedPaymentId: this.encryptedPaymentId, redirectId: this.redirectId})
          .subscribe(authCodeResponse => {
              this.shareService.changeData(authCodeResponse.body);
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
