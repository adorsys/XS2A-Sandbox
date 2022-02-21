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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { TanConfirmationComponent } from './tan-confirmation.component';
import { AccountDetailsComponent } from '../account-details/account-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import get = Reflect.get;
import { ResultPageComponent } from '../result-page/result-page.component';
import { AccountsComponent } from 'src/app/oba/accounts/accounts.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('TanConfirmationComponent', () => {
  let component: TanConfirmationComponent;
  let fixture: ComponentFixture<TanConfirmationComponent>;
  let customizeService: jasmine.SpyObj<CustomizeService>;
  let shareDataService: ShareDataService;
  let ShareDataServiceStub: Partial<ShareDataService>;
  let aisService: jasmine.SpyObj<AisService>;
  let router: Router;
  let route: ActivatedRoute;

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
    };
    const aisServiceSpy = jasmine.createSpyObj('AisService', [
      'revokeConsent',
      'authrizedConsent',
    ]);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [TanConfirmationComponent],
      providers: [
        { provide: CustomizeService, useValue: {} },
        { provide: ShareDataService, useValue: ShareDataServiceStub },
        { provide: AisService, useValue: aisServiceSpy },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
    fixture = TestBed.createComponent(TanConfirmationComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    customizeService = TestBed.inject(
      CustomizeService
    ) as jasmine.SpyObj<CustomizeService>;
    aisService = TestBed.inject(AisService) as jasmine.SpyObj<AisService>;
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // it('should call the on submit with no data', () => {
  //   const mockResponse = {};
  //   component.authResponse = mockResponse;
  //   const result = component.onSubmit();
  //   expect(result).toBeUndefined();
  // });
  //
  // it('should call the on submit', () => {
  //   const mockResponse = {
  //     encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  //     authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //   };
  //   component.authResponse = mockResponse;
  //   component.tanForm.get('authCode').setValue('izsugfHZVblizdwru79348z0');
  //   const pisAuthSpy = spyOn(aisService, 'authrizedConsent').and.returnValue(
  //     of(mockResponse)
  //   );
  //   const navigateSpy = spyOn(router, 'navigate').and.returnValue(
  //     of(undefined).toPromise()
  //   );
  //   component.onSubmit();
  //   expect(navigateSpy).toHaveBeenCalledWith(
  //     [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
  //     {
  //       queryParams: {
  //         encryptedConsentId: undefined,
  //         authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //         oauth2: null,
  //       },
  //     }
  //   );
  //   expect(pisAuthSpy).toHaveBeenCalled();
  // });
  //
  // it('should call the on submit and return to result page when you set a wrong TAN', () => {
  //   const mockResponse = {
  //     encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  //     authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //   };
  //   component.authResponse = mockResponse;
  //   component.tanForm
  //     .get('authCode')
  //     .setValue('izsugfHZVblizdwru79348z0fHZVblizdwru793');
  //   component.invalidTanCount = 3;
  //   const pisAuthSpy = spyOn(aisService, 'authrizedConsent').and.returnValue(
  //     throwError(mockResponse)
  //   );
  //   const error = of(undefined).toPromise();
  //   const errorSpy = spyOn(error, 'then');
  //   const navigateSpy = spyOn(router, 'navigate').and.returnValue(error);
  //   component.onSubmit();
  //   expect(navigateSpy).toHaveBeenCalledWith(
  //     [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
  //     {
  //       queryParams: {
  //         encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  //         authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //         oauth2: null,
  //       },
  //     }
  //   );
  //   expect(errorSpy).toHaveBeenCalled();
  //   expect(pisAuthSpy).toHaveBeenCalled();
  // });
  //
  // it('should cancel and redirect to result page', () => {
  //   const mockResponse = {
  //     encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  //     authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //   };
  //   component.authResponse = mockResponse;
  //   const revokeSpy = spyOn(aisService, 'revokeConsent').and.returnValue(
  //     of(mockResponse)
  //   );
  //   const navigateSpy = spyOn(router, 'navigate').and.returnValue(
  //     of(undefined).toPromise()
  //   );
  //   component.onCancel();
  //   expect(navigateSpy).toHaveBeenCalledWith(
  //     [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
  //     {
  //       queryParams: {
  //         encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
  //         authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
  //       },
  //     }
  //   );
  // });
});
