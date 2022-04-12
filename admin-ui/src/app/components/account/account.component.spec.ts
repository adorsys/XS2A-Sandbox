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

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { IconModule } from '@commons/icon/icon.module';
import { InfoModule } from '@commons/info/info.module';
import { InfoService } from '@commons/info/info.service';
import {
  AccountStatus,
  AccountType,
  UsageType,
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { AccountComponent } from './account.component';
import { ConvertBalancePipe } from '../../pipes/convertBalance.pipe';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TppManagementService } from '../../services/tpp-management.service';
import { TppUserService } from '../../services/tpp.user.service';

describe('AccountComponent', () => {
  let component: AccountComponent;
  let fixture: ComponentFixture<AccountComponent>;
  let accountService: AccountService;
  let infoService: InfoService;
  let tppService: TppManagementService;
  let tppUserService: TppUserService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          HttpClientTestingModule,
          InfoModule,
          IconModule,
        ],
        declarations: [AccountComponent, ConvertBalancePipe],
        providers: [
          AccountService,
          NgbModal,
          TppManagementService,
          InfoService,
          TppUserService,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountComponent);
    component = fixture.componentInstance;
    infoService = TestBed.inject(InfoService);
    accountService = TestBed.inject(AccountService);
    tppService = TestBed.inject(TppManagementService);
    tppUserService = TestBed.inject(TppUserService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getUserInfo on ngOnInit', () => {
    const getTppUserServiceSpy = spyOn(
      tppUserService,
      'getUserInfo'
    ).and.callThrough();
    component.ngOnInit();
    expect(getTppUserServiceSpy).toHaveBeenCalled();
  });

  it('should check if account is deleted', () => {
    component.accountReport = {
      details: {
        branch: 'asdas',
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
        accountStatus: AccountStatus.DELETED,
        bic: 'BIChgdgd',
        usageType: UsageType.PRIV,
        details: '',
        linkedAccounts: '',
        balances: [],
        creditLimit: undefined,
      },
      accesses: [
        {
          userLogin: 'xxxxx',
          scaWeight: 4,
        },
      ],
      multilevelScaEnabled: false,
    };
    const deleteSpy = spyOn(
      tppService,
      'deleteAccountTransactions'
    ).and.returnValue(of({ id: component.accountReport.details.id }));
    const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');

    component.deleteAccountTransactions();
    expect(deleteSpy).toHaveBeenCalledTimes(1);
    expect(infoServiceOpenFeedbackSpy).toHaveBeenCalledWith(
      `Transactions of ${component.accountReport.details.iban} successfully deleted`,
      {
        severity: 'info',
      }
    );
  });

  it('should go to account detail', () => {
    component.accountReport = {
      details: {
        branch: 'asdas',
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
        accountStatus: AccountStatus.DELETED || AccountStatus.BLOCKED,
        bic: 'BIChgdgd',
        usageType: UsageType.PRIV,
        details: '',
        linkedAccounts: '',
        balances: [],
        creditLimit: undefined,
      },
      accesses: [
        {
          userLogin: 'xxxxx',
          scaWeight: 4,
        },
      ],
      multilevelScaEnabled: false,
    };
    const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');
    component.goToAccountDetail();
    expect(
      infoServiceOpenFeedbackSpy
    ).toHaveBeenCalledWith(
      'You can not Grant Accesses to a Deleted/Blocked account',
      { severity: 'error' }
    );
  });

  it('should check if account is deleted', () => {
    component.accountReport = {
      details: {
        branch: 'asdas',
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
        accountStatus: AccountStatus.DELETED || AccountStatus.BLOCKED,
        bic: 'BIChgdgd',
        usageType: UsageType.PRIV,
        details: '',
        linkedAccounts: '',
        balances: [],
        creditLimit: undefined,
      },
      accesses: [
        {
          userLogin: 'xxxxx',
          scaWeight: 4,
        },
      ],
      multilevelScaEnabled: false,
    };
    expect(component.isAccountDeleted).toEqual(true);
  });

  it('should assign account-report after server call', () => {
    const accountReport = {
      details: {
        branch: 'asdas',
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
        creditLimit: undefined,
      },
      accesses: [
        {
          userLogin: 'xxxxx',
          scaWeight: 4,
        },
      ],
      multilevelScaEnabled: false,
    };

    spyOn(accountService, 'getAccountReport').and.returnValue(
      of(accountReport)
    );
    component.getAccountReport();
    expect(component.getAccountReport).not.toBeUndefined();
  });
});
