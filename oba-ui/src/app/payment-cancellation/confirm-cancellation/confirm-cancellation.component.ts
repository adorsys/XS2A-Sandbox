import {Component, OnInit} from '@angular/core';
import {ConsentAuthorizeResponse} from "../../api/models/consent-authorize-response";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ShareDataService} from "../../common/services/share-data.service";
import {AccountDetailsTO} from "../../api/models/account-details-to";
import {RoutingPath} from "../../common/models/routing-path.model";

@Component({
  selector: 'app-confirm-cancellation',
  templateUrl: './confirm-cancellation.component.html',
  styleUrls: ['./confirm-cancellation.component.scss']
})
export class ConfirmCancellationComponent implements OnInit {

  public authResponse: ConsentAuthorizeResponse;
  public encryptedConsentId: string;
  public authorisationId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService) {
  }

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
    this.router.navigate([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.SELECT_SCA}`]);
  }

  public onCancel(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
