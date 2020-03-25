import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { CustomizeService, Theme } from './customize.service';

describe('CustomizeService', () => {
  let service: CustomizeService;
  let httpTestingController: HttpTestingController;
  const defTheme: Theme = {
    globalSettings: {
      logo: 'Logo_XS2ASandbox.png'
    }
  };
  const defUserTheme = {
    globalSettings: {
      logo: ''
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomizeService],
    });
    service = TestBed.get(CustomizeService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get JSON theme', async () => {
    service.getJSON().subscribe(res => {
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

    const req = httpTestingController.expectOne(
      '../assets/UI/custom/UITheme.json'
    );
    expect(req.request.method).toEqual('GET');
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
    service.getDefaultTheme().then(res => {
      expect(res).not.toBeUndefined();
      expect(service.validateTheme(res).length).toEqual(0);
      expect(res).toEqual(defTheme);
    });

    const req = httpTestingController.expectOne(
      '../assets/UI/defaultTheme.json'
    );
    expect(req.request.method).toEqual('GET');
  });

  it('should set user theme', () => {
    service.setUserTheme(defTheme);

    expect(service.getTheme()).toEqual(defTheme);
  });

  it('should return logo name', () => {
    expect(typeof service.getLogo()).toBe('string');
  });

  it('should change font', async done => {
    service.setUserTheme({
      ...defTheme,
      globalSettings: {
        ...defTheme.globalSettings,
        cssVariables: {
          fontFamily: 'Helvetica, Arial, sans-serif',
        },
      },
    });
    setTimeout(() => {
      const tmp = getComputedStyle(document.body).getPropertyValue(
        '--fontFamily'
      );
      expect(tmp).toEqual('Helvetica, Arial, sans-serif');
      done();
    }, 100);
  });

  it('should left default', async done => {
    document.documentElement.removeAttribute('style');
    service.setUserTheme(defTheme);
    setTimeout(() => {
      const tmp = getComputedStyle(document.body).getPropertyValue(
        '--fontFamily'
      );
      expect(tmp).toEqual(' "Verdana", sans-serif');
      done();
    }, 100);
  });

  it('should validate theme', () => {
    let tmp = service.validateTheme(defTheme).length;
    expect(tmp).toEqual(0);

    tmp = service.validateTheme({}).length;
    expect(tmp).not.toEqual(0);
  });
});
