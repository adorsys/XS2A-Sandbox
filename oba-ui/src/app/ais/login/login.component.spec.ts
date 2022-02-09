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
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { of, throwError } from 'rxjs';
import { InfoService } from '../../common/info/info.service';
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
  let aisService: AisService;
  let infoService: InfoService;
  let customizeService: CustomizeService;
  let shareDataService: ShareDataService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule, InfoModule],
        declarations: [LoginComponent],
        providers: [
          AisService,
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
    aisService = TestBed.inject(AisService);
    infoService = TestBed.inject(InfoService);
    router = TestBed.inject(Router);
    router.initialNavigation();
    route = TestBed.inject(ActivatedRoute);
    customizeService = TestBed.inject(CustomizeService);
    shareDataService = TestBed.inject(ShareDataService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on Submit', () => {
    const mockResponse = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    const aisSpy = spyOn(component, 'aisAuthorise');
    component.onSubmit();
    expect(aisSpy).toHaveBeenCalled();
  });

  it('should get the AIS Authorize', () => {
    const loginUsingPOSTParams = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getAisAuthCode').and.callFake(() => {});
    const aisAuthSpy = spyOn(aisService, 'aisAuthorise').and.returnValue(
      of(loginUsingPOSTParams)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.aisAuthorise(loginUsingPOSTParams);
    expect(aisAuthSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.GRANT_CONSENT}`,
    ]);
  });

  it('should call the ais Authorize and return the feedback message when encryptedConsentId is undefined', () => {
    const loginUsingPOSTParams = {
      encryptedConsentId: undefined,
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getAisAuthCode').and.callFake(() => {});
    const aisAuthSpy = spyOn(aisService, 'aisAuthorise').and.returnValue(
      throwError({})
    );
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.aisAuthorise(loginUsingPOSTParams);
    //  expect(aisAuthSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Consent data is missing. Please create consent prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should get the aisAuthCode', () => {
    const mockAuthCodeResponse = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      redirectId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      headers: {
        get: (param) => {
          return 'auth_token';
        },
      },
    };
    const codeSpy = spyOn(aisService, 'aisAuthCode').and.returnValue(
      of<any>(mockAuthCodeResponse)
    );
    component.getAisAuthCode();
  });
});
