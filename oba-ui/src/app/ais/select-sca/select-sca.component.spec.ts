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
import { SelectScaComponent } from './select-sca.component';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AccountDetailsComponent } from '../account-details/account-details.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { RoutingPath } from '../../common/models/routing-path.model';
import { of } from 'rxjs';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let shareDataService: ShareDataService;
  let customizeService: CustomizeService;
  let aisService: AisService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule],
        declarations: [SelectScaComponent, AccountDetailsComponent],
        providers: [
          ShareDataService,
          AisService,
          CustomizeService,
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    aisService = TestBed.inject(AisService);
    customizeService = TestBed.inject(CustomizeService);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit with no data and return', () => {
    const mockResponse = {};
    component.authResponse = mockResponse;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  it('should call the on submit and go to TAN page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      scaMethodId: '123456',
    };
    component.authResponse = mockResponse;
    component.selectedScaMethod = {
      scaMethod: 'EMAIL',
    };
    const selectSPy = spyOn(aisService, 'selectScaMethod').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(selectSPy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.TAN_CONFIRMATION}`,
    ]);
  });

  it('should cancel and redirect to result page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const aisCancelSpy = spyOn(aisService, 'revokeConsent').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
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
