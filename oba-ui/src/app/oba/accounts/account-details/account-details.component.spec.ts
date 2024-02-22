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
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {
  NgbDatepickerModule,
  NgbPaginationModule,
} from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { AuthService } from '../../../common/services/auth.service';
import { AccountDetailsComponent } from './account-details.component';
import { OnlineBankingService } from '../../../common/services/online-banking.service';
import { ConvertBalancePipe } from '../../../pipes/convertBalance.pipe';

describe('AccountDetailsComponent', () => {
  let component: AccountDetailsComponent;
  let fixture: ComponentFixture<AccountDetailsComponent>;
  let onlineBankingService: OnlineBankingService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountDetailsComponent, ConvertBalancePipe],
        imports: [
          RouterTestingModule,
          HttpClientTestingModule,
          ReactiveFormsModule,
          NgbDatepickerModule,
          NgbPaginationModule,
        ],
        providers: [AuthService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailsComponent);
    component = fixture.componentInstance;
    onlineBankingService = TestBed.inject(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the account detail', () => {
    const mockAccount = {
      id: '123456',
      iban: 'DE35653635635663',
      bban: 'BBBAN',
      pan: 'pan',
      maskedPan: 'maskedPan',
      currency: 'EUR',
      msisdn: 'MSISDN',
      name: 'Pupkin',
      product: 'Deposit',
      accountType: 'CASH',
      accountStatus: 'ENABLED',
      bic: 'BIChgdgd',
      usageType: 'PRIV',
      details: '',
      linkedAccounts: '',
      balances: [{ amount: 6767 }],
    };
    const accountSpy = spyOn(
      onlineBankingService,
      'getAccount'
    ).and.returnValue(of<any>(mockAccount));
    component.getAccountDetail();
    expect(accountSpy).toHaveBeenCalled();
  });

  it('should get the Transactions', () => {
    const mockResponse = {};
    const transactionsSpy = spyOn(
      onlineBankingService,
      'getTransactions'
    ).and.returnValue(of(mockResponse));
    component.getTransactions(5, 10);
    expect(transactionsSpy).toHaveBeenCalled();
  });

  it('should change the page', () => {
    const transactionsSpy = spyOn(component, 'getTransactions');
    component.pageChange(10);
    expect(transactionsSpy).toHaveBeenCalledWith(10, 10);
  });
});
