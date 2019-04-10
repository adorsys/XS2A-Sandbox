import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ConsentAuthorizeResponse} from "../../api/models/consent-authorize-response";
import {ScaUserDataTO} from "../../api/models/sca-user-data-to";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AisService} from "../../common/services/ais.service";
import {PisService} from "../../common/services/pis.service";
import {PisCancellationService} from "../../common/services/pis-cancellation.service";
import {ShareDataService} from "../../common/services/share-data.service";
import {RoutingPath} from "../../common/models/routing-path.model";
import {ObaUtils} from "../../common/utils/oba-utils";

@Component({
  selector: 'app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss']
})
export class SelectScaComponent implements OnInit, OnDestroy {

  public authResponse: ConsentAuthorizeResponse;
  public selectedScaMethod: ScaUserDataTO;
  public scaForm: FormGroup;

  public encryptedConsentId: string;
  public authorisationId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private aisService: AisService,
    private pisService: PisService,
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
    this.activatedRoute.queryParams.subscribe(params => {
      this.encryptedConsentId = params['encryptedConsentId'];
      this.authorisationId = params['authorisationId'];
    });

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
      this.aisService.selectScaMethod({
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
        scaMethodId: this.selectedScaMethod.id
      }).subscribe(authResponse => {
        this.authResponse = authResponse;
        this.shareService.changeData(this.authResponse);
        this.router.navigate([`${RoutingPath.TAN_CONFIRMATION}`]);
      })
    );
  }

  // TODO: move to Oba Util https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/9
  public onCancel(): void {
    this.router.navigate([`${RoutingPath.RESULT}`]);
    this.aisService.revokeConsent({
      encryptedConsentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId
    }).subscribe(authResponse => {
      this.shareService.changeData(authResponse);
      this.router.navigate([`${RoutingPath.RESULT}`]);
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
