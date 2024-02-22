/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { InfoModule } from '@commons/info/info.module';
import { InfoService } from '@commons/info/info.service';
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
          BrowserAnimationsModule
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
    accountService = TestBed.inject(AccountService);
    testDataGenerationService = TestBed.inject(TestDataGenerationService);
    infoService = TestBed.inject(InfoService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('create account when form submitted', () => {
    const mockAccount: Account = {
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
    spyOn(accountService, 'createAccount').and.returnValue(of(mockAccount));
    component.onSubmit();

    expect(component.submitted).toBeTruthy();
    expect(component.errorMessage).toEqual(null);
  });

  it('should initiale the currencies List', () => {
    const data = {};
    const currenciesSpy = spyOn(
      accountService,
      'getCurrencies'
    ).and.returnValue(of({ currencies: data }));
    component.initializeCurrenciesList();
    expect(currenciesSpy).toHaveBeenCalled();
  });

  it('should generate Iban', () => {
    const infoSpy = spyOn(infoService, 'openFeedback').and.returnValue();
    const generateSpy = spyOn(
      testDataGenerationService,
      'generateIban'
    ).and.returnValue(of('DE75512108001245126199'));
    component.generateIban();
    expect(infoSpy).toHaveBeenCalledWith(
      'IBAN has been successfully generated'
    );

    expect(generateSpy).toHaveBeenCalled();
  });
});
