/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { HttpClientModule } from '@angular/common/http';
import { TestBed, inject } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { environment } from '@environment/environment';
import { Amount } from '../models/amount.model';
import { GrantAccountAccess } from '../models/grant-account-access.model';
import { Account } from '../models/account.model';
import { AccountService } from './account.service';
import { AccountStatus, AccountType, UsageType } from '../models/account.model';

describe('AccountService', () => {
  let httpMock: HttpTestingController;
  let accountService: AccountService;
  const url = `${environment.tppAdminBackend}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccountService],
    });
    accountService = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    const accountService: AccountService = TestBed.get(AccountService);
    expect(accountService).toBeTruthy();
  });

  it('should get the account', () => {
    accountService.getAccount('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + '/accounts/' + 'accountId');
    expect(req.request.method).toBe('GET');
    req.flush('accountId');
    httpMock.verify();
  });

  it('should return expected list of users (HttpClient called once)', () => {
    const mockAccounts: Account = {
      branch: 'branch',
      id: 'id',
      iban: 'DE12 1234 5678 9012 3456 00',
      bban: 'bban',
      pan: 'pan',
      maskedPan: 'maskedPan',
      msisdn: 'msisdn',
      currency: 'EUR',
      name: 'name',
      product: 'product',
      accountType: AccountType.CACC,
      accountStatus: AccountStatus.BLOCKED,
      bic: 'bic',
      linkedAccounts: 'linkedAccounts',
      usageType: UsageType.ORGA,
      details: 'details',
      balances: [],
      creditLimit: undefined,
    };

    accountService.getAccounts(0, 25, '').subscribe((resp) => {
      const account: any = resp.accounts;
      expect(account.iban).toEqual('DE12 1234 5678 9012 3456 00');
      expect(resp.totalElements).toEqual(Object.keys(mockAccounts).length);
    });
    const req = httpMock.expectOne(
      `${url}/accounts/page?page=${0}&size=${25}&queryParam=${''}&withBalance=true`
    );
    expect(req.cancelled).toBeFalsy();
    expect(req.request.responseType).toEqual('json');
    expect(req.request.method).toEqual('GET');
    req.flush({
      content: mockAccounts,
      totalElements: Object.keys(mockAccounts).length,
    });
  });

  it('should get the accountReport by id', () => {
    accountService.getAccountReport('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + '/accounts/report/' + 'accountId');
    expect(req.request.method).toBe('GET');
    req.flush('accountId');
    httpMock.verify();
  });

  it('should put the updateAccountAccessForUser', () => {
    const mockAccountAccess: GrantAccountAccess = {
      id: 'id',
      accessType: 'OWNER',
      iban: 'DE12 1234 5678 9012 3456 00',
      scaWeight: 50,
    };
    accountService
      .updateAccountAccessForUser(mockAccountAccess)
      .subscribe((data: any) => {
        expect(data.iban).toBe('DE12 1234 5678 9012 3456 00');
      });
    const req = httpMock.expectOne(url + '/accounts/access');
    expect(req.request.method).toBe('PUT');
    req.flush({ iban: 'DE12 1234 5678 9012 3456 00' });
    httpMock.verify();
  });

  it('should post the depositCash', () => {
    const mockAmount: Amount = {
      currency: 'EUR',
      amount: 100,
    };
    accountService
      .depositCash('accountId', mockAmount)
      .subscribe((data: any) => {
        expect(data.amount).toBe(100);
      });
    const req = httpMock.expectOne(
      url + '/accounts/' + 'accountId' + '/deposit-cash'
    );
    expect(req.request.method).toBe('POST');
    req.flush({ amount: 100 });
    httpMock.verify();
  });

  it('should create a account', () => {
    const mockAccount: Account = {
      branch: 'branch',
      id: 'id',
      iban: 'DE12 1234 5678 9012 3456 00',
      bban: 'bban',
      pan: 'pan',
      maskedPan: 'maskedPan',
      msisdn: 'msisdn',
      currency: 'EUR',
      name: 'name',
      product: 'product',
      accountType: AccountType.CACC,
      accountStatus: AccountStatus.BLOCKED,
      bic: 'bic',
      linkedAccounts: 'linkedAccounts',
      usageType: UsageType.ORGA,
      details: 'details',
      balances: [],
      creditLimit: undefined,
    };
    accountService
      .createAccount('accountId', mockAccount)
      .subscribe((data: any) => {
        expect(data.iban).toBe('DE12 1234 5678 9012 3456 00');
      });
  });
});
