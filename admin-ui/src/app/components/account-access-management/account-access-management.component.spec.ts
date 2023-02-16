/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

/* eslint-disable @typescript-eslint/no-unused-vars */
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { User } from '../../models/user.model';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountAccessManagementComponent } from './account-access-management.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AccountService } from '../../services/account.service';
import { InfoModule } from '@commons/info/info.module';
import { InfoService } from '@commons/info/info.service';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import {
  Account,
  AccountStatus,
  AccountType,
  UsageType,
} from '../../models/account.model';
import { Observable, of } from 'rxjs';
import { TppManagementService } from '../../services/tpp-management.service';
import { TppQueryParams } from '../../models/tpp-management.model';

const mockRouter = {
  navigate: (url: string) => {
    console.log('mocknavigation', url);
  },
};
const mockActivatedRoute = {
  params: of({ id: '12345' }),
  queryParams: of({ tppId: '12345' }),
};

const tppManagementServiceMock = {
  getAllUsers: function (
    page: number,
    size: number,
    queryParams?: TppQueryParams
  ): Observable<any> {
    const mockUsers: User[] = [
      {
        id: 'USERID',
        email: 'user@gmail.com',
        login: 'user',
        branch: 'branch',
        pin: '12345',
        scaUserData: [],
        accountAccesses: [],
        branchLogin: 'branchLogin',
      },
    ];
    const userResponse = { users: mockUsers };
    return of(userResponse);
  },
};

describe('AccountAccessManagementComponent', () => {
  let component: AccountAccessManagementComponent;
  let fixture: ComponentFixture<AccountAccessManagementComponent>;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          HttpClientTestingModule,
          NgbTypeaheadModule,
          InfoModule,
          FormsModule,
        ],
        declarations: [AccountAccessManagementComponent],
        providers: [
          AccountService,
          InfoService,
          { provide: TppManagementService, use: tppManagementServiceMock },
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      }).compileComponents();

      fixture = TestBed.createComponent(AccountAccessManagementComponent);
      component = fixture.componentInstance;

      component.ngOnInit();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set the validform of accountAccessForm in OnSumbit', () => {
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
      usageType: UsageType.PRIV,
      details: '',
      linkedAccounts: '',
      balances: [],
    } as Account;
    component.account = mockAccount;
    component.accountAccessForm.get('id').setValue('12345');
    component.accountAccessForm.get('scaWeight').setValue(20);
    component.accountAccessForm.get('accessType').setValue('READ');
    component.onSubmit();
    expect(component.accountAccessForm.invalid).toBeFalsy();
  });

  it('submitted should false', () => {
    expect(component.submitted).toBeFalsy();
  });

  it('accountAccessForm should be invalid when at least one field is empty', () => {
    expect(component.accountAccessForm.valid).toBeFalsy();
  });

  it('validate onSubmit method', () => {
    component.onSubmit();
    expect(component.submitted).toEqual(true);
    expect(component.accountAccessForm.valid).toBeFalsy();
  });

  it('validate setupAccountAccessFormControl method', () => {
    component.setupAccountAccessFormControl();
    expect(component.accountAccessForm).toBeDefined();
  });

  it('should call the inputFormatterValue', () => {
    const mockUser: User = {
      id: 'USERID',
      email: 'user@gmail.com',
      login: 'user',
      branch: 'branch',
      pin: '12345',
      scaUserData: [],
      accountAccesses: [],
      branchLogin: 'branchLogin',
    };
    component.inputFormatterValue(mockUser);
    expect(mockUser.login).toEqual('user');
  });

  it('should return a inputFormatterValue ', () => {
    const mockUser: User = {
      id: 'USERID',
      email: 'user@gmail.com',
      login: '',
      branch: 'branch',
      pin: '12345',
      scaUserData: [],
      accountAccesses: [],
      branchLogin: 'branchLogin',
    };
    component.inputFormatterValue(mockUser);
    expect(component.inputFormatterValue(mockUser)).toBe(mockUser.login);
  });

  it('should return a resultFormatterValue ', () => {
    const mockUser: User = {
      id: 'USERID',
      email: 'user@gmail.com',
      login: 'user',
      branch: 'branch',
      pin: '12345',
      scaUserData: [],
      accountAccesses: [],
      branchLogin: 'branchLogin',
    };
    component.resultFormatterValue(mockUser);
    expect(mockUser.login).toEqual('user');
  });
});
