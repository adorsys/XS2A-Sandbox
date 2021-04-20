import { TestBed } from '@angular/core/testing';

import { PisCancellationService } from './pis-cancellation.service';
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
import LoginUsingPOST2Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params;
import SelectMethodUsingPOST1Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params;
import AuthorisePaymentUsingPOSTParams = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams;

describe('PisCancellationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PisCancellationService = TestBed.inject(
      PisCancellationService
    );
    expect(service).toBeTruthy();
  });

  it('should call the cancellationService', () => {
    const service: PisCancellationService = TestBed.inject(
      PisCancellationService
    );
    const pisCancellationService = TestBed.inject(
      PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
    );
    const mockLogin: LoginUsingPOST2Params = {
      encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
      authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
      pin: '12345',
      login: 'foo',
      Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
    };
    const loginUsingPOST2Spy = spyOn(pisCancellationService, 'loginUsingPOST2');
    service.pisCancellationLogin(mockLogin);
    expect(loginUsingPOST2Spy).toHaveBeenCalledWith(mockLogin);
  });

  it('should call the selectScaMethod', () => {
    const service: PisCancellationService = TestBed.inject(
      PisCancellationService
    );
    const pisCancellationService = TestBed.inject(
      PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
    );
    const mockSelectMethod: SelectMethodUsingPOST1Params = {
      encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
      authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
      scaMethodId: '12345',
      Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
    };
    const selectMethodSpy = spyOn(
      pisCancellationService,
      'selectMethodUsingPOST1'
    );
    service.selectScaMethod(mockSelectMethod);
    expect(selectMethodSpy).toHaveBeenCalledWith(mockSelectMethod);
  });

  it('should call the authorizePayment', () => {
    const service: PisCancellationService = TestBed.inject(
      PisCancellationService
    );
    const pisCancellationService = TestBed.inject(
      PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
    );
    const mockAuth: AuthorisePaymentUsingPOSTParams = {
      encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
      authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
      authCode: '9834zthufwir 9374gfiuw0483kdsfhbvTZUUNOkp43#wrh8',
      Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
    };
    const authSpy = spyOn(pisCancellationService, 'authorisePaymentUsingPOST');
    service.authorizePayment(mockAuth);
    expect(authSpy).toHaveBeenCalledWith(mockAuth);
  });

  it('should call the pisCancellationDone', () => {
    const service: PisCancellationService = TestBed.inject(
      PisCancellationService
    );
    const pisCancellationService = TestBed.inject(
      PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
    );
    const mockPisDone: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams = {
      encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
      authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
      authConfirmationCode: '1234567890',
      Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
    };
    const doneSpy = spyOn(pisCancellationService, 'pisDoneUsingGET');
    service.pisCancellationDone(mockPisDone);
    expect(doneSpy).toBeDefined();
  });
});
