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
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CustomizeService } from '../../common/services/customize.service';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { InfoService } from '../../common/info/info.service';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { of, throwError } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingPath } from '../../common/models/routing-path.model';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let shareDataService: ShareDataService;
  let customizeService: CustomizeService;
  let pisCancellationService: PisCancellationService;
  let pisService: PisService;
  let infoService: InfoService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule, InfoModule],
        declarations: [LoginComponent],
        providers: [PisCancellationService, CustomizeService, PisService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    customizeService = TestBed.inject(CustomizeService);
    pisService = TestBed.inject(PisService);
    infoService = TestBed.inject(InfoService);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    pisCancellationService = TestBed.inject(PisCancellationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the onsubmit', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(of(mockResponse));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    // expect(navigateSpy).toHaveBeenCalledWith([
    //   `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.CONFIRM_CANCELLATION}`,
    // ]);
  });

  it('should get the PIS AuthCode', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    const pisAuthSpy = spyOn(pisService, 'pisAuthCode').and.returnValue(
      of<any>(mockResponse)
    );
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    component.getPisAuthCode();
  });

  it('should call the on submit and return the feedback message whne authorised is undefined', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: undefined,
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(throwError({}));
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment cancellation prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should call the on submit and return the feedback message', () => {
    const mockResponse = {
      encryptedPaymentId: undefined,
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(throwError({}));
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment cancellation prior to login',
      {
        severity: 'error',
      }
    );
  });
});
