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
/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-unused-vars */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { ShareDataService } from '../../common/services/share-data.service';
import { TanConfirmationComponent } from './tan-confirmation.component';
import { PisService } from '../../common/services/pis.service';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { PsupisprovidesGetPsuAccsService } from '../../api/services/psupisprovides-get-psu-accs.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { MatDialog } from '@angular/material/dialog';
import { RoutingPath } from '../../common/models/routing-path.model';

const mockRouter = {
  navigate: () => {},
};

const dialog = {
  open() {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('TanConfirmationComponent', () => {
  let component: TanConfirmationComponent;
  let fixture: ComponentFixture<TanConfirmationComponent>;
  let ShareDataServiceStub: Partial<ShareDataService>;
  let router: Router;
  const pisServiceSpy = jasmine.createSpyObj('PisService', [
    'authorizePayment',
  ]);
  beforeEach(() => {
    ShareDataServiceStub = {
      get currentOperation(): Observable<string> {
        const subjectMock = new BehaviorSubject<string>(null);
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
      get oauthParam(): Observable<boolean> {
        const subjectMock = new BehaviorSubject<boolean>(true);
        return subjectMock.asObservable();
      },

      changeData(data: ConsentAuthorizeResponse) {
        if (data) {
          this.data?.next(data);
        }
      },
    };
    const pisAccServicesSpy = jasmine.createSpyObj(
      'PsupisprovidesGetPsuAccsService',
      ['choseIbanAndCurrency']
    );

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [TanConfirmationComponent],
      providers: [
        { provide: ShareDataService, useValue: ShareDataServiceStub },
        { provide: PisService, useValue: pisServiceSpy },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatDialog, useValue: dialog },
        {
          provide: PsupisprovidesGetPsuAccsService,
          useValue: pisAccServicesSpy,
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(TanConfirmationComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /* it('should call the on submit', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    component.tanForm.get('authCode').setValue('izsugfHZVblizdwru79348z0');
    const pisAuthSpy = pisServiceSpy.authorizePayment.and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
    component.onSubmit();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: undefined,
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
          oauth2: true,
        },
      }
    );
    expect(pisAuthSpy).toHaveBeenCalled();
  });*/

  it('should call the on submit and return to result page when you set a wrong TAN', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    component.tanForm
      .get('authCode')
      .setValue('izsugfHZVblizdwru79348z0fHZVblizdwru793');
    component.invalidTanCount = 3;
    const pisAuthSpy = pisServiceSpy.authorizePayment.and.returnValue(
      throwError(mockResponse)
    );
    const errorDialogSpy = spyOn(dialog, 'open');
    component.onSubmit();
    expect(errorDialogSpy).toHaveBeenCalled();
    expect(pisAuthSpy).toHaveBeenCalled();
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
