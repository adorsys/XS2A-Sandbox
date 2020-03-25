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
      footerLogo: 'Logo_XS2ASandbox.png'
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

  it('custom should return boolean', () => {
    expect(typeof service.custom).toBe('boolean');
  });

  it('should validate theme', () => {
    let tmp = CustomizeService.validateTheme(defTheme).length;
    expect(tmp).toEqual(0);

    tmp = CustomizeService.validateTheme({}).length;
    expect(tmp).not.toEqual(0);
  });
});
