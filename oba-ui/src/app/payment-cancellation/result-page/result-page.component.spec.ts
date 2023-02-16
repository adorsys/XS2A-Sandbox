/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import { ShareDataService } from '../../common/services/share-data.service';
import { PisService } from '../../common/services/pis.service';
import { SettingsService } from '../../common/services/settings.service';
import { ResultPageComponent } from './result-page.component';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';

describe('ResultPageComponent', () => {
  let component: ResultPageComponent;
  let fixture: ComponentFixture<ResultPageComponent>;
  let shareDataService: ShareDataService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [ResultPageComponent],
        providers: [ShareDataService, SettingsService, PisService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultPageComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the ng on init', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      oauth2: undefined,
    };
    const mockPaymentAuthorization: PaymentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.changePaymentData(mockPaymentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.scaStatus).toEqual(mockPaymentAuthorization.scaStatus);
    expect(component.authResponse).toBeDefined();
  });
});
