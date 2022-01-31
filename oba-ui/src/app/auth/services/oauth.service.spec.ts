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

import { TestBed } from '@angular/core/testing';

import { OauthService } from './oauth.service';
import { OnlineBankingOauthAuthorizationService } from '../../api/services/online-banking-oauth-authorization.service';
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OauthService', () => {
  let service: OauthService;
  let onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [OauthService, OnlineBankingOauthAuthorizationService],
    });
    service = TestBed.inject(OauthService);
    onlineBankingOauthAuthorizationService = TestBed.inject(
      OnlineBankingOauthAuthorizationService
    );
  });

  const correctOauthParams: OauthCodeUsingPOSTParams = {
    redirectUri: 'https://adorsys.de/',
    pin: 'pin',
    login: 'login',
  };

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should check that the functions are defined', () => {
    expect(service).toBeTruthy();
    expect(service.authorize).not.toBeNull();
  });

  it('should call onlineBankingOauthAuthorizationService', () => {
    const obaOauthSpy = spyOn(
      onlineBankingOauthAuthorizationService,
      'oauthCodeUsingPOST'
    ).and.returnValue(of<any>('expected result'));
    service.authorize(correctOauthParams);
    expect(obaOauthSpy).toHaveBeenCalledTimes(1);
  });

  it('should authorize', () => {
    const expectedResult = of<any>('expected result');
    const obaOauthSpy = spyOn(
      onlineBankingOauthAuthorizationService,
      'oauthCodeUsingPOST'
    ).and.returnValue(expectedResult);

    const actualResult = service.authorize(correctOauthParams);
    expect(actualResult).not.toBeNull();
    expect(actualResult).toEqual(expectedResult);
    expect(obaOauthSpy).toHaveBeenCalledTimes(1);
  });
});
