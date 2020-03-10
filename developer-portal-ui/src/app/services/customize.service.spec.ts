import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { CustomizeService } from './customize.service';
import { Theme } from '../models/theme.model';

describe('CustomizeService', () => {
  let service: CustomizeService;
  let httpTestingController: HttpTestingController;
  const defTheme: Theme = {
    globalSettings: {
      logo: 'Logo_XS2ASandbox.png',
      footerLogo: 'Logo_XS2ASandbox.png',
      facebook: 'https://www.facebook.com/adorsysGmbH/',
      linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
    },
    contactInfo: {
      img: 'Rene.png',
      name: 'René Pongratz',
      position: 'Software Architect & Expert for API Management',
      email: 'psd2@adorsys.de',
    },
    officesInfo: [
      {
        city: 'Nürnberg',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Fürther Str. 246a, Gebäude 32 im 4.OG',
        addressSecondLine: '90429 Nürnberg',
        phone: '+49(0)911 360698-0',
        email: 'psd2@adorsys.de',
      },
      {
        city: 'Frankfurt',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Frankfurter Straße 63 - 69',
        addressSecondLine: '65760 Eschborn',
        email: 'frankfurt@adorsys.de',
        facebook: 'https://www.facebook.com/adorsysGmbH/',
        linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
      },
    ],
    supportedLanguagesDictionary: {
      en: "united-kingdom.png",
      de: "germany.png",
      es: "spain.png",
      ua: "ukraine.png"
    },
    supportedApproaches: ['redirect', 'embedded'],
    currency: 'EUR',
    tppSettings: {
      tppDefaultNokRedirectUrl: 'https://www.google.com',
      tppDefaultRedirectUrl:
        'https://adorsys-platform.de/solutions/xs2a-sandbox/',
    },
  };
  const defUserTheme = {
    globalSettings: {
      logo: '',
      footerLogo: '',
    },
    contactInfo: {
      img: '',
      name: '',
      position: '',
    },
    officesInfo: [
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
    ],
    supportedLanguagesDictionary: {},
    supportedApproaches: [],
    currency: '',
    tppSettings: {
      tppDefaultNokRedirectUrl: '',
      tppDefaultRedirectUrl: '',
    },
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
    service.getJSON().then(res => {
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

  it('isCustom should return boolean', () => {
    expect(typeof service.isCustom()).toBe('boolean');
  });

  it('getTheme should return default or user theme, user theme should be valid', () => {
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

    const req = httpTestingController.expectOne('assets/UI/defaultTheme.json');
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
      expect(tmp).toEqual(' Arial, sans-serif');
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
