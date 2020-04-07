import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavComponent} from './nav.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpLoaderFactory, LanguageService} from "../../../services/language.service";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Pipe, PipeTransform} from "@angular/core";
import {DataService} from "../../../services/data.service";
import {NavigationService} from "../../../services/navigation.service";
import {of} from "rxjs";
import {CustomizeService} from "../../../services/customize.service";

describe('NavComponent', () => {
  let component: NavComponent;
  let fixture: ComponentFixture<NavComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  const CustomizeServiceStub = {
    custom: () => false,
    currentTheme: of({
      globalSettings: {
        logo: '../assets/content/Logo_XS2ASandbox.png',
        footerLogo: '../assets/content/Logo_XS2ASandbox.png',
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
      tppSettings: {
        tppDefaultNokRedirectUrl: 'https://www.google.com',
        tppDefaultRedirectUrl:
          'https://adorsys-platform.de/solutions/xs2a-sandbox/',
      },
      supportedLanguages: ['en', 'ua', 'de', 'es'],
      pagesSettings: {
        contactPageSettings: {
          showContactCard: true,
          showQuestionsComponent: true
        },
        homePageSettings: {
          showQuestionsComponent: true,
          showProductHistory: true,
          showSlider: true
        },
        navigationBarSettings: {
          allowedNavigationSize: 3
        }
      }
    }),
    setStyling: (theme) => {
    },
    normalizeLanguages: (theme) => {
      return CustomizeServiceStub.currentTheme.toPromise()
    }
  };

  const DataServiceStub = {
    setRouterUrl: (val: string) => {
    },
    getRouterUrl: () => '',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavComponent,
        TranslatePipe
      ],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          }
        })],
      providers: [
        LanguageService,
        TranslateService,
        NavigationService,
        {provide: DataService, useValue: DataServiceStub},
        {provide: CustomizeService, useValue: CustomizeServiceStub},
      ],

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change language', () => {
    component.supportedLanguages = ['en'];
    expect(component.getLangCollapsed()).toBeTruthy();
    component.changeLang('ua');
    expect(component.language).toEqual('ua');
  });

  it('should collapse', () => {
    let prevCollapseStatus;
    for (let i = 0; i < 2; i++) {
      prevCollapseStatus = component.getLangCollapsed();
      component.collapseThis();
      expect(component.getLangCollapsed).not.toEqual(prevCollapseStatus);
    }
  });
});
