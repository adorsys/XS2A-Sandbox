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

import { PisCancellationService } from './pis-cancellation.service';

describe('PisCancellationService', () => {
  let service: PisCancellationService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PisCancellationService],
    });

    service = TestBed.inject(PisCancellationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // it('should call the cancellationService', () => {
  //   const service: PisCancellationService = TestBed.inject(
  //     PisCancellationService
  //   );
  //   const pisCancellationService = TestBed.inject(
  //     PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
  //   );
  //   const mockLogin: LoginUsingPOST2Params = {
  //     encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
  //     authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
  //     pin: '12345',
  //     login: 'foo',
  //     Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
  //   };
  //   const loginUsingPOST2Spy = spyOn(pisCancellationService, 'loginUsingPOST2');
  //   service.pisCancellationLogin(mockLogin);
  //   expect(loginUsingPOST2Spy).toHaveBeenCalledWith(mockLogin);
  // });
  //
  // it('should call the selectScaMethod', () => {
  //   const service: PisCancellationService = TestBed.inject(
  //     PisCancellationService
  //   );
  //   const pisCancellationService = TestBed.inject(
  //     PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
  //   );
  //   const mockSelectMethod: SelectMethodUsingPOST1Params = {
  //     encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
  //     authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
  //     scaMethodId: '12345',
  //     Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
  //   };
  //   const selectMethodSpy = spyOn(
  //     pisCancellationService,
  //     'selectMethodUsingPOST1'
  //   );
  //   service.selectScaMethod(mockSelectMethod);
  //   expect(selectMethodSpy).toHaveBeenCalledWith(mockSelectMethod);
  // });
  //
  // it('should call the authorizePayment', () => {
  //   const service: PisCancellationService = TestBed.inject(
  //     PisCancellationService
  //   );
  //   const pisCancellationService = TestBed.inject(
  //     PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
  //   );
  //   const mockAuth: AuthorisePaymentUsingPOSTParams = {
  //     encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
  //     authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
  //     authCode: '9834zthufwir 9374gfiuw0483kdsfhbvTZUUNOkp43#wrh8',
  //     Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
  //   };
  //   const authSpy = spyOn(pisCancellationService, 'authorisePaymentUsingPOST');
  //   service.authorizePayment(mockAuth);
  //   expect(authSpy).toHaveBeenCalledWith(mockAuth);
  // });
  //
  // it('should call the pisCancellationDone', () => {
  //   const service: PisCancellationService = TestBed.inject(
  //     PisCancellationService
  //   );
  //   const pisCancellationService = TestBed.inject(
  //     PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
  //   );
  //   const mockPisDone: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams = {
  //     encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
  //     authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
  //     authConfirmationCode: '1234567890',
  //     Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
  //   };
  //   const doneSpy = spyOn(pisCancellationService, 'pisDoneUsingGET');
  //   service.pisCancellationDone(mockPisDone);
  //   expect(doneSpy).toBeDefined();
  // });
});
