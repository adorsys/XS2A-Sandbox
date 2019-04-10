import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountDetailsTO } from '../api/models';
import { RoutingPath } from '../common/models/routing-path.model';
import { AisService } from '../common/services/ais.service';
import { ShareDataService } from '../common/services/share-data.service';
import { ObaUtils } from '../common/utils/oba-utils';
import { ConsentAuthorizeResponse } from '../api/models';
import { Subscription } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-bank-offered',
  templateUrl: './bank-offered.component.html',
  styleUrls: ['./bank-offered.component.scss']
})
export class BankOfferedComponent implements OnInit {

  public authResponse: ConsentAuthorizeResponse;
  public operation: string;
  public encryptedConsentId: string;
  public authorisationId: string;
  public bankOfferedForm: FormGroup;
  public bankOffered: boolean;
  private subscriptions: Subscription[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private aisService: AisService,
    private shareService: ShareDataService) {
    this.bankOfferedForm = this.formBuilder.group({});
  }

  public ngOnInit(): void {

    this.shareService.currentData.subscribe(data => {
      if (data) {
        this.shareService.currentData.subscribe(authResponse => {
          this.authResponse = authResponse;
          this.bankOffered = this.isBankOfferedConsent();
        });
      }
    });
  }

  public onSubmit() {
    if (!this.authResponse) {
      console.log('Missing application data');
      return;
    }
    this.subscriptions.push(
      this.aisService.startConsentAuth({
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
        aisConsent: this.authResponse.consent,
      }).subscribe(authResponse => {
        this.authResponse = authResponse;
        this.shareService.changeData(this.authResponse);
        this.router.navigate([`${RoutingPath.SELECT_SCA}`]);
      })
    );
  }

  public cancel(): void {
    this.aisService.revokeConsent({
      encryptedConsentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId
    }).subscribe(authResponse => {
      console.log(authResponse);
      this.authResponse = authResponse;
      this.shareService.changeData(this.authResponse);
      this.router.navigate([`${RoutingPath.RESULT}`]);
    });
  }

  handleObjectSelectedEvent(value, container): void {
    const idx = container.indexOf(value);
    if (idx > -1) { // is currently selected
      container.splice(idx, 1);
    } else { // is newly selected
      container.push(value);
    }
  }

  public accountsChecked(account): boolean {
    return this.authResponse.consent.access.accounts.indexOf(account.iban) > -1
  }

  public balancesChecked(account): boolean {
    return this.authResponse.consent.access.balances.indexOf(account.iban) > -1;
  }

  public transactionsChecked(account): boolean {
    return this.authResponse.consent.access.transactions.indexOf(account.iban) > -1;
  }

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  get consentAccounts(): Array<string> {
    return this.authResponse.consent.access.accounts;
  }

  get consentBalances(): Array<string> {
    return this.authResponse.consent.access.balances;
  }

  get consentTransactions(): Array<string> {
    return this.authResponse.consent.access.transactions;
  }

  private isBankOfferedConsent() {
    return this.isEmptyAccountAccess() && this.isEmptyBalancesAccess() && this.isEmptyTransactionsAccess();
  }

  private isEmptyAccountAccess(): boolean {
    return this.authResponse.consent.access.accounts == null ||
      this.authResponse.consent.access.accounts.length == 0;
  }

  private isEmptyBalancesAccess(): boolean {
    return this.authResponse.consent.access.balances == null ||
      this.authResponse.consent.access.balances.length == 0;
  }

  private isEmptyTransactionsAccess(): boolean {
    return this.authResponse.consent.access.transactions == null ||
      this.authResponse.consent.access.transactions.length == 0;
  }

}
