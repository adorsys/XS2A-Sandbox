// /*
//  * Copyright 2018-2023 adorsys GmbH & Co KG
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
// import { ComponentFixture, TestBed } from '@angular/core/testing';
//
// import { PaymentDetailsComponent } from './payment-details.component';
// import { RouterTestingModule } from '@angular/router/testing';
// import {
//   ICurrencyAndIban,
//   PsupisprovidesGetPsuAccsService
// } from '../../api/services/psupisprovides-get-psu-accs.service';
// import { ShareDataService } from '../../common/services/share-data.service';
// import { BehaviorSubject, Observable } from 'rxjs';
// import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
// import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
//
// describe('PaymentDetailsComponent', () => {
//   let component: PaymentDetailsComponent;
//   let fixture: ComponentFixture<PaymentDetailsComponent>;
//   let pisService: PsupisprovidesGetPsuAccsService;
//   let pisServiceMock: Partial<PsupisprovidesGetPsuAccsService>;
//   let shareDataService: ShareDataService;
//   let shareDataServiceMock: Partial<ShareDataService>;
//
//   beforeEach(() => {
//     pisServiceMock = {
//       getIsSubmitted(): Observable<boolean> {
//         const subjectMock = new BehaviorSubject<boolean>(null);
//         return subjectMock.asObservable();
//       },
//       get choseIbanAndCurrency(): ICurrencyAndIban {
//         const subjectMock = new BehaviorSubject<ICurrencyAndIban>(null);
//         return subjectMock.getValue();
//       },
//       getAllIban(): Observable<unknown> {
//         const subjectMock = new BehaviorSubject<unknown>(null);
//         return subjectMock.asObservable();
//       }
//     };
//     shareDataServiceMock = {
//       get currentData(): Observable<ConsentAuthorizeResponse | PaymentAuthorizeResponse> {
//         const subjectMock = new BehaviorSubject<ConsentAuthorizeResponse | PaymentAuthorizeResponse>(null);
//         return subjectMock.asObservable();
//       }
//     };
//
//     TestBed.configureTestingModule({
//       declarations: [PaymentDetailsComponent],
//       imports: [RouterTestingModule],
//       providers: [
//         { provide: ShareDataService, useValue: shareDataServiceMock },
//         { provide: PsupisprovidesGetPsuAccsService, useValue: pisServiceMock }
//       ]
//     }).compileComponents();
//     fixture = TestBed.createComponent(PaymentDetailsComponent);
//     pisService = TestBed.inject(PsupisprovidesGetPsuAccsService);
//     shareDataService = TestBed.inject(ShareDataService);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });
//
//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });
//
//   // it('should return empty when no auth response for accounts', () => {
//   //   component.authResponse = null;
//   //   const result = component.totalAmount;
//   //   expect(result).toBe(0);
//   // });
//
//   // it('should return 0 when auth response for accounts has no payment', () => {
//   //   component.authResponse = {
//   //     payment: null,
//   //   };
//   //   const result = component.totalAmount;
//   //   expect(result).toBe(0);
//   // });
//
//   // it('should return totalAmount when auth response for accounts has payments', () => {
//   //   component.authResponse = {
//   //     payment: {
//   //       targets: [
//   //         {
//   //           instructedAmount: {
//   //             amount: 15,
//   //           },
//   //         },
//   //         {
//   //           instructedAmount: {
//   //             amount: 110,
//   //           },
//   //         },
//   //       ],
//   //     },
//   //   };
//   //   const result = component.totalAmount;
//   //   expect(result).toBe(125);
//   // });
// });
