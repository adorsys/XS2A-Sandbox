import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {
  HttpLoaderFactory,
  LanguageService,
} from '../services/language.service';
import { HttpClient } from '@angular/common/http';
import { DataService } from '../services/data.service';
import { CustomizeService } from '../services/customize.service';
import { Pipe, PipeTransform } from '@angular/core';
import { Router } from '@angular/router';
import { of } from 'rxjs';

const TRANSLATIONS_EN = require('../assets/i18n/en.json');
const TRANSLATIONS_DE = require('../assets/i18n/de.json');
const TRANSLATIONS_ES = require('../assets/i18n/es.json');
const TRANSLATIONS_UA = require('../assets/i18n/ua.json');

describe('AppComponent', () => {
  let comp: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let translate: TranslateService;
  let http: HttpTestingController;
  let languageService: LanguageService;
  let dataService: DataService;
  let customizeService: CustomizeService;
  let router: Router;

  const DataServiceStub = {
    setRouterUrl: (val: string) => {},
    getRouterUrl: () => '',
  };

  const CustomizeServiceStub = {
    isCustom: () => false,
    setUserTheme: () => {},
    getJSON: () => {
      return new Promise(resolve => {
        resolve({
          globalSettings: {
            logo: '../assets/UI/Logo_XS2ASandbox.png',
            cssVariables: {
              colorPrimary: '#054f72',
              colorSecondary: '#eed52f',
              fontFamily: 'Arial, sans-serif',
              headerBG: '#ffffff',
              headerFontColor: '#000000',
              footerBG: '#054f72',
              footerFontColor: '#ffffff',
            },
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
              linkedIn:
                'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
            },
          ],
        });
      });
    },
    getLogo: () => '../assets/UI/Logo_XS2ASandbox.png',
  };

  const LanguageServiceStub = {
    language: 'en',
    initializeTranslation: () => {},
    setLang: (lang: string) => (LanguageServiceStub.language = lang),
    getLang: () => LanguageServiceStub.language,
    getLanguages: () => of(['en', 'de', 'es', 'ua']),
  };

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppComponent, TranslatePipe],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          },
        }),
      ],
      providers: [
        TranslateService,
        { provide: DataService, useValue: DataServiceStub },
        { provide: CustomizeService, useValue: CustomizeServiceStub },
        { provide: LanguageService, useValue: LanguageServiceStub },
      ],
    })
      .compileComponents()
      .then(() => {
        translate = TestBed.get(TranslateService);
        http = TestBed.get(HttpTestingController);
        dataService = TestBed.get(DataService);
        customizeService = TestBed.get(CustomizeService);
        languageService = TestBed.get(LanguageService);
        router = TestBed.get(Router);
      });
  }));

  beforeEach(async(() => {
    fixture = TestBed.createComponent(AppComponent);
    comp = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', async(() => {
    expect(comp).toBeTruthy();
  }));

  it('should change lang', () => {
    comp.langs = ['en'];
    expect(comp.getLangCollapsed()).toBeTruthy();
    comp.changeLang('ua');
    expect(comp.lang).toEqual('ua');
  });

  it('should collapse', () => {
    let prevCollapseStatus;
    for (let i = 0; i < 2; i++) {
      prevCollapseStatus = comp.getLangCollapsed();
      comp.collapseThis();
      expect(comp.getLangCollapsed).not.toEqual(prevCollapseStatus);
    }
  });

  it('should set global settings in ngOnInit', () => {
    const getGlobalSettingsSpy = spyOn(
      customizeService,
      'getJSON'
    ).and.callThrough();

    comp.ngOnInit();

    expect(getGlobalSettingsSpy).toHaveBeenCalled();
    expect(comp.globalSettings).not.toBeUndefined();
  });
});
