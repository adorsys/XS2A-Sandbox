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
import { By } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgHttpLoaderModule } from 'ng-http-loader';
import { of } from 'rxjs';
import { Title } from '@angular/platform-browser';
import { ApiConfiguration } from './api/api-configuration';
import { AppComponent } from './app.component';
import { URL_PARAMS_PROVIDER } from './common/constants/constants';
import { CustomizeService } from './common/services/customize.service';
import { ShareDataService } from './common/services/share-data.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let customizeService: CustomizeService;
  let titleService: Title;
  const CustomizeServiceStub = {
    isCustom: () => false,
    setUserTheme: () => {},
    getJSON: () => {
      return of({
        globalSettings: {
          logo: '../assets/UI/Logo_XS2ASandbox.png',
          cssVariables: {
            colorPrimary: '#054f72',
            fontFamily: 'Arial, sans-serif',
            headerBG: '#ffffff',
            headerFontColor: '#000000',
          },
        },
      });
    },
    getLogo: () => '../assets/UI/Logo_XS2ASandbox.png',
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AppComponent],
        imports: [RouterTestingModule, NgHttpLoaderModule.forRoot()],
        providers: [
          ApiConfiguration,
          { provide: URL_PARAMS_PROVIDER, useValue: {} },
          { provide: CustomizeService, useValue: CustomizeServiceStub },
          ShareDataService,
          Title,
        ],
      })
        .compileComponents()
        .then(() => {
          customizeService = TestBed.inject(CustomizeService);
        });
    })
  );
  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    titleService = TestBed.inject(Title);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it(
    'Should include the < router-outlet >',
    waitForAsync(() => {
      fixture.detectChanges();
      const debugElement = fixture.debugElement.query(
        By.directive(RouterOutlet)
      );
      expect(debugElement).not.toBeNull();
    })
  );

  it('should set global settings in ngOnInit', () => {
    const getGlobalSettingsSpy = spyOn(
      customizeService,
      'getJSON'
    ).and.callThrough();

    component.ngOnInit();

    expect(getGlobalSettingsSpy).toHaveBeenCalled();
    expect(component.globalSettings).not.toBeUndefined();
  });

  it('should check the Url', () => {
    const mockUrl = '/login';
    component.checkUrl();
    expect(mockUrl).toEqual('/login');
  });

  it('should check the Url Bank', () => {
    const mockUrl = '/bank-offered';
    component.checkUrlbank();
    expect(mockUrl).toEqual('/bank-offered');
  });
});
