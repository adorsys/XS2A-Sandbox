// /*
//  * Copyright 2018-2022 adorsys GmbH & Co KG
//  *
//  * This program is free software: you can redistribute it and/or modify it
//  * under the terms of the GNU Affero General Public License as published
//  * by the Free Software Foundation, either version 3 of the License, or (at
//  * your option) any later version. This program is distributed in the hope that
//  * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
//  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  * See the GNU Affero General Public License for more details.
//  *
//  * You should have received a copy of the GNU Affero General Public License
//  * along with this program. If not, see https://www.gnu.org/licenses/.
//  *
//  * This project is also available under a separate commercial license. You can
//  * contact us at psd2@adorsys.com.
//  */
//
// import { TestBed } from '@angular/core/testing';
// import { PisService } from './pis.service';
// import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';
// import PisAuthUsingGETParams = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams;
// import LoginUsingPOST3Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params;
// import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
// // tslint:disable-next-line:max-line-length
// import AuthorisePaymentUsingPOSTParams = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams;
// import PisDoneUsingGET1Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params;
//
// describe('PisService', () => {
//   let pisService: PisService;
//   let pisServicePSU: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService;
//   beforeEach(() => {
//     pisService = TestBed.inject(PisService);
//     pisServicePSU = TestBed.inject(
//       PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService
//     );
//   });
//
//   it('should be created', () => {
//     expect(pisService).toBeTruthy();
//   });
//
//   it('should call the pis AuthCode', () => {
//     const mockPisAuth: PisAuthUsingGETParams = {
//       redirectId: 'ierzbvbrivuzer869fzgvFDZUIUZGAVOSp72ftgiqehfbq',
//       encryptedPaymentId:
//         'vwhjkrbv86oFZIVGSHRJHVAZOXVWGREIIlwjrnvvwrwrnjvnlwrvnwrjnvLKN',
//       Authorization: 'iuo3gqaberve78roiguUIBZAOIDBHKJLKNSDJKIVSFBDV',
//     };
//     const pisAuthSpy = spyOn(pisServicePSU, 'pisAuthUsingGETResponse');
//     pisService.pisAuthCode(mockPisAuth);
//     expect(pisAuthSpy).toHaveBeenCalledWith(mockPisAuth);
//   });
//
//   it('should call the ais authorize', () => {
//     const mockLogin: LoginUsingPOST3Params = {
//       encryptedPaymentId: 'igbdvcg8679230zHUDhsjkldbv978soibv3on9urgvbeu',
//       authorisationId: 'iwbhv7809sugibpf8h20n384gvbeiO(/GS/p',
//       pin: '12345',
//       login: 'foo',
//       Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
//     };
//     const loginUsingPOSTSpy = spyOn(pisServicePSU, 'loginUsingPOST3');
//     pisService.pisLogin(mockLogin);
//     expect(loginUsingPOSTSpy).toHaveBeenCalledWith(mockLogin);
//   });
//
//   it('should call the select sca Method', () => {
//     const mockScaMethod: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params = {
//       authorisationId: 'ierzbvbrivuzer869fzgvFDZUIUZGAVOSp72ftgiqehfbq',
//       encryptedPaymentId:
//         'vwhjkrbv86oFZIVGSHRJHVAZOXVWGREIIlwjrnvvwrwrnjvnlwrvnwrjnvLKN',
//       scaMethodId: '123456',
//       Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
//     };
//     const selectSpy = spyOn(pisServicePSU, 'selectMethodUsingPOST2');
//     pisService.selectScaMethod(mockScaMethod);
//     expect(selectSpy).toHaveBeenCalledWith(mockScaMethod);
//   });
//
//   it('should call the authorize consent', () => {
//     const mockAuthConsent: AuthorisePaymentUsingPOSTParams = {
//       authorisationId: 'ierzbvbrivuzer869fzgvFDZUIUZGAVOSp72ftgiqehfbq',
//       encryptedPaymentId:
//         'vwhjkrbv86oFZIVGSHRJHVAZOXVWGREIIlwjrnvvwrwrnjvnlwrvnwrjnvLKN',
//       authCode: 'lwuirbfgowbrfwowcnfub3479wneupno9B7QwN 89ZUAB OS79tbdwdwdq',
//       Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
//     };
//     const authSpy = spyOn(pisServicePSU, 'authrizedPaymentUsingPOST');
//     pisService.authorizePayment(mockAuthConsent);
//     expect(authSpy).toHaveBeenCalledWith(mockAuthConsent);
//   });
//
//   it('should call the ais done', () => {
//     const mockAuthConsent: PisDoneUsingGET1Params = {
//       authorisationId: 'ierzbvbrivuzer869fzgvFDZUIUZGAVOSp72ftgiqehfbq',
//       encryptedPaymentId:
//         'vwhjkrbv86oFZIVGSHRJHVAZOXVWGREIIlwjrnvvwrwrnjvnlwrvnwrjnvLKN',
//       Cookie: 'gf2ziuoge79tUGJDX^^8s9upgidouqwlejbf910qieflnqebf',
//       authConfirmationCode: 'igowufvbjksfbGZIVAZVDG7829z4rhjkBAhvdlhasva',
//       oauth2: false,
//     };
//     const authSpy = spyOn(pisServicePSU, 'pisDoneUsingGET1');
//     pisService.pisDone(mockAuthConsent);
//     expect(authSpy).toHaveBeenCalledWith(mockAuthConsent);
//   });
// });
