import {Component, OnDestroy, OnInit} from '@angular/core';
import {PaymentAuthorizeResponse} from "../../api/models/payment-authorize-response";
import {ScaUserDataTO} from "../../api/models/sca-user-data-to";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ShareDataService} from "../../common/services/share-data.service";
import {RoutingPath} from "../../common/models/routing-path.model";
import {PisCancellationService} from "../../common/services/pis-cancellation.service";

@Component({
  selector: 'app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss']
})
export class SelectScaComponent implements OnInit, OnDestroy {

  public authResponse: PaymentAuthorizeResponse;
  public selectedScaMethod: ScaUserDataTO;
  public scaForm: FormGroup;

  private subscriptions: Subscription[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private pisCancellationService: PisCancellationService,
    private shareService: ShareDataService) {
    this.scaForm = this.formBuilder.group({
      scaMethod: ['', Validators.required],
    });
  }

  get scaMehods(): ScaUserDataTO[] {
    return this.authResponse ? this.authResponse.scaMethods : [];
  }

  ngOnInit() {
    // fetch data that we save before after login
    this.shareService.currentData.subscribe(data => {
      if (data) {
        // TODO extract the Accounts, Balances and Transactions from data.value https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/9
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
      this.pisCancellationService.selectScaMethod({
        encryptedPaymentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
        scaMethodId: this.selectedScaMethod.id
      }).subscribe(authResponse => {
        this.authResponse = authResponse;
        this.shareService.changeData(this.authResponse);
        this.router.navigate([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.TAN_CONFIRMATION}`]);
      })
    );
  }

  public onCancel(): void {
    this.router.navigate([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`], {
      queryParams: {
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId
      }
    });
  }

  handleMethodSelectedEvent(scaMethod: ScaUserDataTO): void {
    console.log('No sca method selected.');
    this.selectedScaMethod = scaMethod;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
