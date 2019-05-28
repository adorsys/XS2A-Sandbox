import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ShareDataService} from "../../common/services/share-data.service";
import {PisService} from "../../common/services/pis.service";
import {RoutingPath} from "../../common/models/routing-path.model";
import {PisCancellationService} from "../../common/services/pis-cancellation.service";
import {PSUPISCancellationService} from "../../api/services/psupiscancellation.service";
import LoginUsingPOST1Params = PSUPISCancellationService.LoginUsingPOST1Params;
import {InfoService} from "../../common/info/info.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  invalidCredentials: boolean;

  private encryptedPaymentId: string;
  private redirectId: string;

  private subscriptions: Subscription[] = [];


  constructor(private formBuilder: FormBuilder,
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
      } as LoginUsingPOST1Params).subscribe(authorisationResponse => {
        console.log(authorisationResponse);
        this.shareService.changeData(authorisationResponse);
        this.router.navigate([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.CONFIRM_CANCELLATION}`]);
      }, (error1: HttpErrorResponse) => {
        // if paymentId or redirectId is missing
        if (this.encryptedPaymentId === undefined || this.redirectId === undefined) {
          this.infoService.openFeedback('Payment data is missing. Please initiate payment cancellation prior to login', {
            severity: 'error'
          });
        } else {
          // else throw error
          throw new HttpErrorResponse(error1);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private getPisAuthCode(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.encryptedPaymentId = params['paymentId'];
      this.redirectId = params['redirectId'];

      this.subscriptions.push(
        this.pisService.pisAuthCode({encryptedPaymentId: this.encryptedPaymentId, redirectId: this.redirectId})
          .subscribe(authCodeResponse => {
              this.shareService.changeData(authCodeResponse);
            },
            (error) => {
              console.log(error);
            })
      );
    })
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }


}
