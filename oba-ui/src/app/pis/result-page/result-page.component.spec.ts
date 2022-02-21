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

import { ResultPageComponent } from './result-page.component';
import { ShareDataService } from '../../common/services/share-data.service';
import { PisService } from '../../common/services/pis.service';
import { SettingsService } from '../../common/services/settings.service';
import { of } from 'rxjs';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';

describe('ResultPageComponent', () => {
  let component: ResultPageComponent;
  let fixture: ComponentFixture<ResultPageComponent>;
  let shareDataService: ShareDataService;
  let settingsService: SettingsService;
  let pisService: PisService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [ResultPageComponent],
        providers: [ShareDataService, PisService, SettingsService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultPageComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    settingsService = TestBed.inject(SettingsService);
    pisService = TestBed.inject(PisService);
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
    const mockConsentAuthorization: ConsentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.changeData(mockConsentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.scaStatus).toEqual(mockConsentAuthorization.scaStatus);
    expect(component.authResponse).toBeDefined();
  });
});
