import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {CustomizeService} from './customize.service';

describe('CustomizeService', () => {
  let service: CustomizeService;
  let httpTestingController: HttpTestingController;
  const defTheme = {
    globalSettings: {
      logo: 'Logo_XS2ASandbox.png',
      fontFamily: 'Arial, sans-serif',
      headerBG: '#ffffff',
      headerFontColor: '#000000',
      footerBG: '#054f72',
      footerFontColor: '#ffffff',
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
  };
  const defUserTheme = {
    globalSettings: {
      logo: '',
      fontFamily: '',
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
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        CustomizeService,
      ]
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
    service.getJSON()
      .then(res => {
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

  it('isCustom should return boolean', () => {
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
    service.getDefaultTheme()
      .then(res => {
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

  it('should change font or left default', () => {
    service.changeFontFamily('Ubuntu');
    let tmp = document.getElementsByTagName('body')[0].getAttribute('style');
    expect(tmp).toEqual('font-family: Ubuntu');

    service.changeFontFamily();
    tmp = document.getElementsByTagName('body')[0].getAttribute('style');
    expect(tmp).toEqual('font-family: ' + defTheme.globalSettings.fontFamily);
  });

  it('should validate theme', () => {
    let tmp = service.validateTheme(defTheme).length;
    expect(tmp).toEqual(0);

    tmp = service.validateTheme({}).length;
    expect(tmp).not.toEqual(0);
  });
});
