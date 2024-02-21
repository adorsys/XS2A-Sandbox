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
/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-unused-vars */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { TanConfirmationComponent } from './tan-confirmation.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { MatDialog } from '@angular/material/dialog';

const mockRouter = {
  navigate: () => {
    return of(true).toPromise();
  },
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

const mockResponse = {
  encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
};

describe('TanConfirmationComponent', () => {
  let component: TanConfirmationComponent;
  let fixture: ComponentFixture<TanConfirmationComponent>;
  let shareDataService: Partial<ShareDataService>;
  let aisService: jasmine.SpyObj<AisService>;
  let aisServiceSpy: jasmine.SpyObj<AisService>;
  let router: Router;

  const dialog = {
    open() {},
  };

  beforeEach(() => {
    shareDataService = {
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
    aisServiceSpy = jasmine.createSpyObj('AisService', [
      'revokeConsent',
      'authrizedConsent',
    ]);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [TanConfirmationComponent],
      providers: [
        { provide: CustomizeService, useValue: {} },
        { provide: ShareDataService, useValue: shareDataService },
        { provide: AisService, useValue: aisServiceSpy },
        { provide: Router, useValue: mockRouter },
        { provide: MatDialog, useValue: dialog },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
    fixture = TestBed.createComponent(TanConfirmationComponent);
    component = fixture.componentInstance;
    aisService = TestBed.inject(AisService) as jasmine.SpyObj<AisService>;
    router = TestBed.inject(Router);
    aisServiceSpy.authrizedConsent.and.returnValue(of(mockResponse));
    aisServiceSpy.revokeConsent.and.returnValue(of(null));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit with no data', () => {
    const mockResponse = {};
    component.authResponse = mockResponse;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  /* it('should call the on submit', () => {
    component.authResponse = mockResponse;
    component.tanForm.get('authCode').setValue('izsugfHZVblizdwru79348z0');

    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
    component.onSubmit();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: undefined,
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
          oauth2: true,
        },
      }
    );
    expect(aisService.authrizedConsent).toHaveBeenCalled();
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
    const pisAuthSpy = aisServiceSpy.authrizedConsent.and.returnValue(
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
    aisServiceSpy.authrizedConsent.and.returnValue(of(mockResponse));

    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
    spyOn(shareDataService, 'changeData').and.returnValue();
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });
});
