import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TestCasesComponent} from './test-cases.component';
import {Pipe, PipeTransform} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {DataService} from '../../services/data.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MarkdownModule} from "ngx-markdown";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpLoaderFactory, LanguageService} from "../../services/language.service";
import {HttpClient} from "@angular/common/http";
import {of} from "rxjs";
import {CustomizeService} from "../../services/customize.service";

describe('TestCasesComponent', () => {
  let component: TestCasesComponent;
  let fixture: ComponentFixture<TestCasesComponent>;

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

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestCasesComponent, TranslatePipe],
      imports: [
        RouterTestingModule,
        MarkdownModule.forRoot(),
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          }
        })
      ],
      providers: [
        LanguageService,
        TranslateService,
        {
          provide: DataService,
          useValue: DataServiceStub
        },
        {
          provide: CustomizeService,
          useValue: CustomizeServiceStub
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should collapse', () => {
    let colHigh = document.getElementById('redirect-content').style.maxHeight;
    component.collapseThis('redirect');
    expect(
      document.getElementById('redirect-content').style.maxHeight
    ).not.toBe(colHigh);
    component.collapseThis('redirect');
    expect(document.getElementById('redirect-content').style.maxHeight).toBe(
      colHigh
    );

    colHigh = document.getElementById('embedded-content').style.maxHeight;
    component.collapseThis('embedded');
    expect(
      document.getElementById('embedded-content').style.maxHeight
    ).not.toBe(colHigh);
    component.collapseThis('embedded');
    expect(document.getElementById('embedded-content').style.maxHeight).toBe(
      colHigh
    );

    colHigh = document.getElementById('account-content').style.maxHeight;
    component.collapseThis('account');
    expect(document.getElementById('account-content').style.maxHeight).not.toBe(
      colHigh
    );
    component.collapseThis('account');
    expect(document.getElementById('account-content').style.maxHeight).toBe(
      colHigh
    );
  });
});
