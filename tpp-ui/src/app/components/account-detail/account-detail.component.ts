import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  AccountStatus,
  AccountType,
  UsageType,
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TestDataGenerationService } from '../../services/test.data.generation.service';
import { InfoService } from '../../commons/info/info.service';

@Component({
  selector: 'app-account-detail',
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss'],
})
export class AccountDetailComponent implements OnInit {
  accountForm = new FormGroup({
    accountType: new FormControl('CASH', Validators.required),
    usageType: new FormControl(UsageType.PRIV, Validators.required),
    currency: new FormControl('EUR', Validators.required),
    iban: new FormControl(null, Validators.required),
    accountStatus: new FormControl(AccountStatus.ENABLED, Validators.required),
  });
  accountTypes = Object.keys(AccountType);
  accountStatuses = Object.keys(AccountStatus);
  usageTypes = Object.keys(UsageType);
  userBranch: string;
  userId: string;
  currencies;
  submitted = false;
  errorMessage = null;

  constructor(
    private accountService: AccountService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private generationService: TestDataGenerationService,
    private infoService: InfoService
  ) {}

  ngOnInit() {
    this.initializeCurrenciesList();
    this.activatedRoute.queryParams.subscribe((params) => {
      this.userBranch = params['tppId'];
      this.userId = params['userId'];
    });
  }

  initializeCurrenciesList() {
    return this.accountService.getCurrencies().subscribe(
      (data) => (this.currencies = data),
      (error) => console.log(error)
    );
  }

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
    this.accountService
      .createAccount(this.userId, this.accountForm.getRawValue())
      .subscribe(() => this.router.navigate(['/accounts']));
  }

  generateIban() {
    return this.generationService
      .generateIban(this.userBranch)
      .subscribe((data) => {
        this.accountForm.get('iban').setValue(data);
        this.infoService.openFeedback('IBAN has been successfully generated');
      });
  }

  onCancel() {
    this.router.navigate(['/users/all']);
  }
}
