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
import { RouterTestingModule } from '@angular/router/testing';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';
import { PaymentDetailsComponent } from '../payment-details/payment-details.component';
import { ConfirmCancellationComponent } from './confirm-cancellation.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { of } from 'rxjs';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('ConfirmCancellationComponent', () => {
  let component: ConfirmCancellationComponent;
  let fixture: ComponentFixture<ConfirmCancellationComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let shareDataService: ShareDataService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [ConfirmCancellationComponent, PaymentDetailsComponent],
        providers: [
          ShareDataService,
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmCancellationComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    shareDataService = TestBed.inject(ShareDataService);
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

  it('should confirm the payment and redirect to select sca page ', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const navigateSpy = spyOn(router, 'navigate');
    component.onConfirm();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.SELECT_SCA}`,
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
      [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });

  it('should call the ng on init', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      oauth2: undefined,
    };
    const mockConsentAuthorization: ConsentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.currentData = of(mockConsentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.authResponse).toEqual(mockConsentAuthorization);
    expect(component.authResponse).toBeDefined();
  });
});
