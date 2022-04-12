import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { CustomizeService } from '../../common/services/customize.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { OnlineBankingOauthAuthorizationService } from '../../api/services/online-banking-oauth-authorization.service';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';
import { PsupisprovidesGetPsuAccsService } from '../../api/services/psupisprovides-get-psu-accs.service';
import { takeUntil } from 'rxjs/operators';
import LoginUsingPOST3Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params;
import PisAuthUsingGETParams = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string;
  invalidCredentials: boolean;

  private encryptedPaymentId: string;
  private redirectId: string;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: FormBuilder,
    private infoService: InfoService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService,
    private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService,
    private pisService: PisService,
    private pisAccServices: PsupisprovidesGetPsuAccsService
  ) {}

  ngOnInit() {
    this.initLoginForm();

    this.getPisAuthCode();
  }

  public onSubmit(): void {
    this.pisAuthorise({
      pin: this.loginForm.get('pin').value,
      login: this.loginForm.get('login').value,
      encryptedPaymentId: this.encryptedPaymentId,
      authorisationId: this.redirectId,
    });
  }

  public pisAuthorise(params: LoginUsingPOST3Params) {
    this.pisService
      .pisLogin(params)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        (authorisationResponse) => {
          this.shareService.changePaymentData(authorisationResponse);
          // if (authorisationResponse?.payment.debtorAccount) {
          //   const {
          //     currency,
          //     iban,
          //   } = authorisationResponse.payment.debtorAccount;
          //   if (Boolean(currency) && Boolean(iban)) {
          //     this.isExistedDebtorAccFromResponse(currency, iban);
          //   }
          // }
          this.router.navigate(
            [
              `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.CONFIRM_PAYMENT}`,
            ],
            {
              queryParams: {
                encryptedPaymentId: this.encryptedPaymentId,
                authorisationId: this.redirectId,
              },
            }
          );
        },
        (error: HttpErrorResponse) => {
          // if paymentId or redirectId is missing
          if (
            this.encryptedPaymentId === undefined ||
            this.redirectId === undefined
          ) {
            this.infoService.openFeedback(
              'Payment data is missing. Please initiate payment prior to login',
              {
                severity: 'error',
              }
            );
          } else {
            if (error.status === 401) {
              this.errorMessage = `You don\'t have access to this account.`;
            } else {
              this.errorMessage = error.error
                ? error.error.message
                : error.message;
            }
            throw new HttpErrorResponse(error);
          }
        }
      );
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  isExistedDebtorAccFromResponse(currency, iban): void {
    this.pisAccServices
      .sendPisInitiate(
        { currency, iban },
        {
          encryptedPaymentId: this.encryptedPaymentId,
          authorisationId: this.redirectId,
        }
      )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((res) => {});
  }

  public getPisAuthCode(): void {
    this.activatedRoute.queryParams
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params) => {
        this.encryptedPaymentId = params.paymentId;
        this.redirectId = params.redirectId;

        // set oauth2 param in shared service
        this.shareService.setOauthParam(!!params.oauth2);

        const pisAuthCodeParams: PisAuthUsingGETParams = {
          encryptedPaymentId: this.encryptedPaymentId,
          redirectId: this.redirectId,
          ...(params.token && { Authorization: 'Bearer ' + params.token }),
        };
      });
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }
}
