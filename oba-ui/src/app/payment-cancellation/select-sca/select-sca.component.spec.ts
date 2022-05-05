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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { PaymentDetailsComponent } from '../payment-details/payment-details.component';
import { SelectScaComponent } from './select-sca.component';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { RoutingPath } from '../../common/models/routing-path.model';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

const mockRouter = {
  navigate: () => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let pisCancellationService: PisCancellationService;
  let router: Router;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule],
        declarations: [SelectScaComponent, PaymentDetailsComponent],
        providers: [
          ShareDataService,
          PisCancellationService,
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    pisCancellationService = TestBed.inject(PisCancellationService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit with no data and return', () => {
    component.authResponse = null;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  it('should call the on submit and go to TAN page', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      scaMethodId: '123456',
    };
    component.selectedScaMethod = {
      scaMethod: 'EMAIL',
    };
    component.authResponse = mockResponse;
    const pisSelectSpy = spyOn(
      pisCancellationService,
      'selectScaMethod'
    ).and.returnValue(of(mockResponse));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(pisSelectSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.TAN_CONFIRMATION}`,
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

  it('should handle Sca Method', () => {
    const mockScaMethod: ScaUserDataTO = {
      decoupled: false,
      id: '123',
      methodValue: '',
      scaMethod: 'EMAIL',
      staticTan: 'foo@foo.de',
      user: {},
      usesStaticTan: true,
      valid: false,
    };
    component.selectedScaMethod = mockScaMethod;
    component.handleMethodSelectedEvent(mockScaMethod);
    expect(component.selectedScaMethod).toEqual(mockScaMethod);
  });
});
