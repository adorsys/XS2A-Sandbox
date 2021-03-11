import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { InfoModule } from '../../commons/info/info.module';
import { InfoService } from '../../commons/info/info.service';
import {
  Account,
  AccountStatus,
  AccountType,
  UsageType,
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { AccountComponent } from '../account/account.component';
import { AccountDetailComponent } from './account-detail.component';
import { ConvertBalancePipe } from 'src/app/pipes/convertBalance.pipe';
import { TestDataGenerationService } from '../../services/test.data.generation.service';

describe('AccountDetailComponent', () => {
  let component: AccountDetailComponent;
  let fixture: ComponentFixture<AccountDetailComponent>;
  let accountService: AccountService;
  let testDataGenerationService: TestDataGenerationService;
  let infoService: InfoService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes([
            { path: 'accounts', component: AccountComponent },
          ]),
          ReactiveFormsModule,
          HttpClientTestingModule,
          InfoModule,
          FormsModule,
        ],
        declarations: [
          AccountDetailComponent,
          AccountComponent,
          ConvertBalancePipe,
        ],
        providers: [AccountService, InfoService, TestDataGenerationService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    accountService = TestBed.get(AccountService);
    testDataGenerationService = TestBed.get(TestDataGenerationService);
    infoService = TestBed.get(InfoService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('create account when form submitted', () => {
    let mockAccount: Account = {
      id: 'XXXXXX',
      iban: 'DE35653635635663',
      bban: 'BBBAN',
      pan: 'pan',
      maskedPan: 'maskedPan',
      currency: 'EUR',
      msisdn: 'MSISDN',
      name: 'Pupkin',
      product: 'Deposit',
      accountType: AccountType.CASH,
      accountStatus: AccountStatus.ENABLED,
      bic: 'BIChgdgd',
      linkedAccounts: '',
      usageType: UsageType.PRIV,
      details: '',
      balances: [],
    } as Account;

    // submit invalid form
    expect(component.accountForm.valid).toBeFalsy();
    component.onSubmit();

    // submit valid form
    component.accountForm.controls['iban'].setValue('DE33737373773737377');
    expect(component.accountForm.valid).toBeTruthy();

    // mock http call
    let createAccountSpy = spyOn(
      accountService,
      'createAccount'
    ).and.returnValue(of(mockAccount));
    component.onSubmit();

    expect(component.submitted).toBeTruthy();
    expect(component.errorMessage).toEqual(null);
  });

  it('should initiale the currencies List', () => {
    let data = {};
    let currenciesSpy = spyOn(accountService, 'getCurrencies').and.returnValue(
      of({ currencies: data })
    );
    component.initializeCurrenciesList();
    expect(currenciesSpy).toHaveBeenCalled();
  });

  it('should generate Iban', () => {
    let data = {};
    let infoSpy = spyOn(infoService, 'openFeedback');
    //let generateSpy = spyOn(testDataGenerationService, 'generateIban').and.returnValue(of({data: infoSpy}));
    component.generateIban();
    expect(infoSpy).toHaveBeenCalledWith(
      'IBAN has been successfully generated'
    );
  });

  it('should initialize a currencies List', () => {
    const currenciesSpy = spyOn(
      accountService,
      'getCurrencies'
    ).and.returnValue(throwError(''));
    component.initializeCurrenciesList();
  });
});
