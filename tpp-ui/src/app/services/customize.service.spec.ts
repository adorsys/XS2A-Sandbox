/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CustomizeService, Theme } from './customize.service';

describe('CustomizeService', () => {
  let service: CustomizeService;
  let httpTestingController: HttpTestingController;
  const defTheme: Theme = {
    globalSettings: {
      logo: 'assets/UI/Logo_XS2ASandbox.png',
    },
  };
  const defUserTheme = {
    globalSettings: {
      logo: '',
    },
  };

  const theme: Theme = {
    globalSettings: {
      logo: 'logog',
      title: 'title',
      favicon: {
        type: 'type',
        href: 'href',
      },
      cssVariables: {
        colorPrimary: 'colorPrimary',
        fontFamily: 'fontFamily',
        bodyBG: 'bodyBG',
        headerBG: 'headerBG',
        headerFontColor: 'headerFontColor',
        sidebarBG: 'sidebarBG',
        sidebarFontColor: 'sidebarFontColor',
        mainBG: 'mainBG',
        anchorFontColor: 'anchorFontColor',
        anchorFontColorHover: 'anchorFontColorHover',
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomizeService],
    });
    service = TestBed.inject(CustomizeService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get JSON theme', async () => {
    service.getJSON().subscribe((res) => {
      if (!service.isCustom()) {
        expect(res).toEqual(defTheme);
      } else {
        expect(service.validateTheme(res).length).toEqual(0);
      }
      if (JSON.stringify(res) !== JSON.stringify(defTheme)) {
        expect(service.isCustom()).toBeFalsy();
      } else {
        expect(service.isCustom()).toBeTruthy();
      }
    });

    const req = httpTestingController.expectOne('../assets/UI/custom/UITheme.json');
    expect(req.request.method).toEqual('GET');
  });

  describe('getJson', () => {
    let http: HttpClient;
    beforeEach(() => {
      http = TestBed.inject(HttpClient);
    });
    it('should return custom theme', () => {
      spyOn(http, 'get').and.returnValue(of(theme));
      service.getJSON().subscribe();
      expect(service.isCustom()).toBeTruthy();
    });

    it('should return default theme when custom theme is invalid', () => {
      const invalidJsonTheme = undefined;
      spyOn(http, 'get').and.returnValue(of(invalidJsonTheme));
      service.getJSON().subscribe();
      expect(service.isCustom()).toBeFalsy();
    });

    it('should return default theme when custom theme has validations error', () => {
      const invalidTheme = {};
      spyOn(http, 'get').and.returnValue(of(invalidTheme));
      service.getJSON().subscribe();
      expect(service.isCustom()).toBeFalsy();
    });
  });

  it('custom should return boolean', () => {
    expect(typeof service.isCustom()).toBe('boolean');
  });

  it('getThem should return default or user theme, user theme should be valid', () => {
    if (!service.isCustom()) {
      expect(service.getTheme()).toEqual(defUserTheme);
    } else {
      expect(service.validateTheme(service.getTheme()).length).toEqual(0);
    }
    expect(service.getTheme('default')).toEqual(defTheme);
  });

  it('getDefaultTheme should return default theme, default theme should be valid', () => {
    service.getDefaultTheme().then((res) => {
      expect(res).not.toBeUndefined();
      expect(service.validateTheme(res).length).toEqual(0);
      expect(res).toEqual(defTheme);
    });

    const req = httpTestingController.expectOne('../assets/UI/defaultTheme.json');
    expect(req.request.method).toEqual('GET');
  });

  it('should set user theme', () => {
    service.setUserTheme(defTheme);

    expect(service.getTheme()).toEqual(defTheme);
  });

  it('should return logo name', () => {
    expect(typeof service.getLogo()).toBe('string');
  });


  it('should change font', fakeAsync(() => {
    service.setUserTheme({
      ...defTheme,
      globalSettings: {
        ...defTheme.globalSettings,
        cssVariables: {
          fontFamily: 'Helvetica, Arial, sans-serif',
        },
      },
    });

    tick(100);

    const tmp = getComputedStyle(document.body).getPropertyValue('--fontFamily');
    expect(tmp).toEqual('Helvetica, Arial, sans-serif');
  }));

  it('should left default', fakeAsync(() => {
    document.documentElement.removeAttribute('style');
    service.setUserTheme(defTheme);
    tick(100);

    const tmp = getComputedStyle(document.body).getPropertyValue('--fontFamily').trim();
    expect(tmp).toEqual('"Verdana", sans-serif');
  }));

  it('should validate theme', () => {
    let tmp = service.validateTheme(defTheme).length;
    expect(tmp).toEqual(0);

    tmp = service.validateTheme({}).length;
    expect(tmp).not.toEqual(0);
  });

  it('should add favicon', () => {
    service.addFavicon('type', 'href');
    const link = document.documentElement.getElementsByTagName('link');
    expect(link).not.toEqual(null);
  });

  it('should set favicon', () => {
    service.setFavicon('type', 'href');
    const link = document.documentElement.getElementsByTagName('link');
    expect(link).not.toEqual(null);
  });
});
