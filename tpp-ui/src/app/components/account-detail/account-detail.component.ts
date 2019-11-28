import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AccountStatus, AccountType, Currency, UsageType} from "../../models/account.model";
import {AccountService} from "../../services/account.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TestDataGenerationService} from "../../services/test.data.generation.service";
import {InfoService} from "../../commons/info/info.service";

@Component({
  selector: 'app-account-detail',
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss']
})
export class AccountDetailComponent implements OnInit {
  accountForm = new FormGroup({
    'accountType': new FormControl('CASH', Validators.required),
    'usageType': new FormControl(UsageType.PRIV, Validators.required),
    'currency': new FormControl(Currency.EUR, Validators.required),
    'iban': new FormControl(null, Validators.required),
    'accountStatus': new FormControl(AccountStatus.ENABLED, Validators.required),
  });
  private userID: string;

  accountTypes = Object.keys(AccountType);
  accountStatuses = Object.keys(AccountStatus);
  usageTypes = Object.keys(UsageType);
  currencies = Object.keys(Currency);
  submitted = false;
  errorMessage = null;

  constructor(
    private accountService: AccountService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private generationService: TestDataGenerationService,
    private infoService: InfoService) {
      this.userID = this.activatedRoute.snapshot.params['id'];
  }

  ngOnInit() { }

  get accountType() {
    return this.accountForm.get('accountType');
  }

  get usageType() {
    return this.accountForm.get('usageType');
  }

  get accountStatus() {
    return this.accountForm.get('accountStatus');
  }

  get iban() {
    if (this.accountForm.get('iban').value) {
      const ibanValue = this.accountForm.get('iban').value;
      this.accountForm.get('iban').setValue(ibanValue.replace(/\s/g, ''));
    }
    return this.accountForm.get('iban');
  }

  get currency() {
    return this.accountForm.get('currency');
  }

  onSubmit() {
    this.submitted = true;
    this.errorMessage = null;
    if (this.accountForm.invalid) {
      return;
    }

    this.accountService.createAccount(this.userID, this.accountForm.getRawValue())
      .subscribe(() => this.router.navigate(['/accounts']));
  }

  generateIban() {
    return this.generationService.generateIban()
        .subscribe(data => {
          this.accountForm.get('iban').setValue(data);
          this.infoService.openFeedback('IBAN has been successfully generated');
        });
  }
}
