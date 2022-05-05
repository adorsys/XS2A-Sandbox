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

import { inject, TestBed } from '@angular/core/testing';
import { JwtHelperService } from '@auth0/angular-jwt';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { AuthService } from './auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { ResetPassword } from '../../api/models/reset-password';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';

describe('AuthService', () => {
  let httpTestingController: HttpTestingController;
  let authService: AuthService;
  let onlineBankingAuth: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        AuthService,
        JwtHelperService,
        OnlineBankingAuthorizationProvidesAccessToOnlineBankingService,
      ],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
    onlineBankingAuth = TestBed.inject(
      OnlineBankingAuthorizationProvidesAccessToOnlineBankingService
    );
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', inject([AuthService], (service: AuthService) => {
    expect(service).toBeTruthy();
  }));

  it('should delete token on logout', () => {
    const navigateSpy = spyOn(router, 'navigate').and.callFake(() =>
      Promise.resolve(true)
    );
    authService.logout();

    expect(sessionStorage.getItem('token')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/logout']);
  });

  it('should call authorize when login', () => {
    const getAuthorizationTokenSpy = spyOn(
      authService,
      'authorize'
    ).and.callThrough();
    const credentialsMock = { login: 's', pin: 'q' };
    authService.login(credentialsMock);
    expect(getAuthorizationTokenSpy).toHaveBeenCalled();
  });

  it('should reset password', () => {
    const resetPasswort = ({
      code: 443,
      email: 'ica@test.de',
      login: 'rue',
      newPassword: 'laksd',
      phone: '+283472342',
    } as unknown) as ResetPassword;
    const spyOnlineBankingAuth = spyOn(
      onlineBankingAuth,
      'updatePasswordUsingPUT'
    );
    authService.resetPassword(resetPasswort);
    expect(spyOnlineBankingAuth).toHaveBeenCalledWith(resetPasswort);
  });
});
