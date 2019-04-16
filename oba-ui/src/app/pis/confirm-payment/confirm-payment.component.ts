import {Component, OnDestroy, OnInit} from '@angular/core';
import {ConsentAuthorizeResponse} from "../../api/models/consent-authorize-response";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {AisService} from "../../common/services/ais.service";
import {ShareDataService} from "../../common/services/share-data.service";
import {AccountDetailsTO} from "../../api/models/account-details-to";
import {RoutingPath} from "../../common/models/routing-path.model";

@Component({
  selector: 'app-confirm-payment',
  templateUrl: './confirm-payment.component.html',
  styleUrls: ['./confirm-payment.component.scss']
})
export class ConfirmPaymentComponent implements OnInit, OnDestroy {

  public authResponse: ConsentAuthorizeResponse;
  public encryptedConsentId: string;
  public authorisationId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private aisService: AisService,
    private shareService: ShareDataService) {}

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  public ngOnInit(): void {
    this.subscriptions.push(
      this.shareService.currentData.subscribe(data => {
        if (data) {
          this.shareService.currentData.subscribe(authResponse => {
            this.authResponse = authResponse;
          });
        }
      })
    );
  }

  public onConfirm() {
    this.router.navigate([`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.SELECT_SCA}`]);
  }

  public onCancel(): void {
    this.router.navigate([`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`], {
      queryParams: {
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
