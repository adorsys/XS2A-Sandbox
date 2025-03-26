/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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
/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-unused-vars */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';
import { ConfirmPaymentComponent } from './confirm-payment.component';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { BehaviorSubject, Observable, of } from 'rxjs';
import {
  ICurrencyAndIban,
  PsupisprovidesGetPsuAccsService,
} from '../../api/services/psupisprovides-get-psu-accs.service';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

const mockRouter = {
  navigate: () => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

const mockResponse = {
  encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  payment: {
    debtorAccount: {
      iban: 'DE80760700240271232400',
      currency: 'EUR',
    },
  },
};

const mockResponseInit = {
  scaStatus: 'psuAuthenticated',
};

describe('ConfirmPaymentComponent', () => {
  let component: ConfirmPaymentComponent;
  let fixture: ComponentFixture<ConfirmPaymentComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let shareDataServiceStub: Partial<ShareDataService>;
  let psiServiceStub: Partial<PsupisprovidesGetPsuAccsService>;

  beforeEach(() => {
    shareDataServiceStub = {
      get oauthParam(): Observable<boolean> {
        const subjectMock = new BehaviorSubject<boolean>(null);
        return subjectMock.asObservable();
      },
      get currentData(): Observable<
        ConsentAuthorizeResponse | PaymentAuthorizeResponse
      > {
        const subjectMock = new BehaviorSubject<
          ConsentAuthorizeResponse | PaymentAuthorizeResponse
        >(null);
        return subjectMock.asObservable();
      },

      changeData(data: ConsentAuthorizeResponse) {
        if (data) {
          this.data?.next(data);
        }
      },
      changePaymentData(data: PaymentAuthorizeResponse) {
        if (data) {
          this.data?.next(data);
        }
      },
    };
    psiServiceStub = {
      choseIbanAndCurrencyObservable(): Observable<ICurrencyAndIban> {
        const subjectMock = new BehaviorSubject<ICurrencyAndIban>(null);
        return subjectMock.asObservable();
      },
      getIsSubmitted(): Observable<boolean> {
        const subjectMock = new BehaviorSubject<boolean>(null);
        return subjectMock.asObservable();
      },
      sendPisInitiate() {
        const subjectMock = new BehaviorSubject<any>(mockResponseInit);
        return subjectMock.asObservable();
      },
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ConfirmPaymentComponent],
      providers: [
        { provide: ShareDataService, useValue: shareDataServiceStub },
        { provide: PsupisprovidesGetPsuAccsService, useValue: psiServiceStub },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: { params: of(mockActivatedRoute), queryParams: of({}) },
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmPaymentComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the accounts', () => {
    component.authResponse = {
      accounts: [{ iban: 'DE12 1234 5678 9000 0005' }],
    };
    const result = component.accounts;
    expect(result).toBeTruthy();
  });

  it('should return empty when no auth response for accounts', () => {
    component.authResponse = null;
    const result = component.accounts;
    expect(result).toEqual([]);
  });

  it('should confirm the payment and redirect to result page without scaMethod', () => {

    component.payAuthResponse = mockResponse;
    mockResponseInit.scaStatus = 'exempted';
    component.transactionStatus = 'ACSP';

    const sendPisInitiateSpy = spyOn(
      component,
      'sendPisInitiate'
    ).and.callThrough();

    const redirectSpy = spyOn(component, 'redirectOnConfirm').and.callThrough();

    const navigateSpy = spyOn(router, 'navigate');

    component.onConfirm();

    expect(sendPisInitiateSpy).toHaveBeenCalledWith([
      'DE80760700240271232400',
      'EUR',
    ]);
    expect(redirectSpy).toHaveBeenCalledWith(mockResponseInit);

    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
          oauth2: null,
        },
      }
    );
  });

  it('should confirm the payment and redirect to select sca page ', () => {
    const mockResponse2 = {
      currency: 'EUR',
      iban: 'DE80760700240271232400',
    };

    mockResponseInit.scaStatus = 'psuAuthenticated';
    component.payAuthResponse = mockResponse;
    component.payAuthResponse.payment.debtorAccount = mockResponse2;

    const redirectSpy = spyOn(component, 'redirectOnConfirm').and.callThrough();
    const navigateSpy = spyOn(router, 'navigate');

    component.onConfirm();
    expect(redirectSpy).toHaveBeenCalledWith(mockResponseInit);
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.SELECT_SCA}`,
    ]);
  });

  it('should cancel and redirect to result page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const navigateSpy = spyOn(router, 'navigate');
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });
});
