import {TestBed} from '@angular/core/testing';

import {OauthService} from './oauth.service';
import {OnlineBankingOauthAuthorizationService} from "../../api/services/online-banking-oauth-authorization.service";
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;
import {of} from "rxjs";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('OauthService', () => {
  let service: OauthService;
  let onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [OauthService, OnlineBankingOauthAuthorizationService],
    });
    service = TestBed.get(OauthService);
    onlineBankingOauthAuthorizationService = TestBed.get(OnlineBankingOauthAuthorizationService);
  });

  const correctOauthParams: OauthCodeUsingPOSTParams = {
    redirectUri: "https://adorsys.de/",
    pin: "pin",
    login: "login"
  };

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should check that the functions are defined', () => {
    expect(service).toBeTruthy();
    expect(service.authorize).not.toBeNull();
  });

  it('should call onlineBankingOauthAuthorizationService', () => {
    let obaOauthSpy = spyOn(onlineBankingOauthAuthorizationService, 'oauthCodeUsingPOST').and.returnValue(of('expected result'));
    service.authorize(correctOauthParams);
    expect(obaOauthSpy).toHaveBeenCalledTimes(1);
  });

  it('should authorize', () => {
    const expectedResult = of('expected result');
    let obaOauthSpy = spyOn(onlineBankingOauthAuthorizationService, 'oauthCodeUsingPOST').and.returnValue(expectedResult);

    const actualResult = service.authorize(correctOauthParams);
    expect(actualResult).not.toBeNull();
    expect(actualResult).toEqual(expectedResult);
    expect(obaOauthSpy).toHaveBeenCalledTimes(1);
  });
});
