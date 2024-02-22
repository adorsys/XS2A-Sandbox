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
import { RouterTestingModule } from '@angular/router/testing';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { AuthService } from '../../common/services/auth.service';
import { AccountsComponent } from './accounts.component';
import { of } from 'rxjs';
describe('AccountsComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  let onlineBankingService: OnlineBankingService;
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'getAuthorizedUser',
    'isLoggedIn',
    'logout',
  ]);

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountsComponent],
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [
          { provide: AuthService, useValue: authServiceSpy },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    onlineBankingService = TestBed.inject(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the accounts list', () => {
    const mockAccounts = [
      {
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
        balances: [],
      },
    ];
    const accountsSpy = spyOn(
      onlineBankingService,
      'getAccounts'
    ).and.returnValue(
      of<any>({ mockAccounts })
    );
    component.getAccounts();
    expect(accountsSpy).toHaveBeenCalled();
  });
});
