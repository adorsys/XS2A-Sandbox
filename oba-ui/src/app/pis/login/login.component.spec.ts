/* eslint-disable @typescript-eslint/no-empty-function */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CustomizeService } from '../../common/services/customize.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { InfoService } from '../../common/info/info.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let shareDataService: ShareDataService;
  let pisService: PisService;
  let router: Router;
  let infoService: InfoService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule, InfoModule],
        declarations: [LoginComponent],
        providers: [
          PisService,
          ShareDataService,
          CustomizeService,
          InfoService,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    pisService = TestBed.inject(PisService);
    shareDataService = TestBed.inject(ShareDataService);
    infoService = TestBed.inject(InfoService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on Submit', () => {
    const pisSpy = spyOn(component, 'pisAuthorise');
    component.onSubmit();
    expect(pisSpy).toHaveBeenCalled();
    spyOn(component, 'isExistedDebtorAccFromResponse').and.callFake(() => {});
  });

  it('should get the PIS Authorize', () => {
    const loginUsingPOSTParams = {
      encryptedPaymentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisAuthSpy = spyOn(pisService, 'pisLogin').and.returnValue(
      of(loginUsingPOSTParams)
    );
    spyOn(router, 'navigate');
    component.pisAuthorise(loginUsingPOSTParams);
    expect(pisAuthSpy).toHaveBeenCalled();
  });

  it('should call the pis Authorize and return the feedback message when encryptedConsentId is undefined', () => {
    const loginUsingPOSTParams = {
      encryptedPaymentId: undefined,
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisAuthSpy = spyOn(pisService, 'pisLogin').and.returnValue(
      throwError({})
    );
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.pisAuthorise(loginUsingPOSTParams);
    expect(pisAuthSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should get the pisAuthCode', () => {
    const codeSpy = spyOn(shareDataService, 'setOauthParam');
    component.getPisAuthCode();
    expect(codeSpy).toHaveBeenCalled();
  });
});
