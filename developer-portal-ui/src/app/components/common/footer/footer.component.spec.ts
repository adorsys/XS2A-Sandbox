import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FooterComponent } from './footer.component';
import { Pipe, PipeTransform } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CustomizeService } from '../../../services/customize.service';
import { of } from 'rxjs';
import { MarkdownModule } from 'ngx-markdown';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../services/language.service';
import { HttpClient } from '@angular/common/http';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

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
          linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
        },
      ],
      tppSettings: {
        tppDefaultNokRedirectUrl: 'https://www.google.com',
        tppDefaultRedirectUrl: 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
      },
      supportedLanguages: ['en', 'ua', 'de', 'es'],
      pagesSettings: {
        contactPageSettings: {
          showContactCard: true,
          showQuestionsComponent: true,
        },
        homePageSettings: {
          showQuestionsComponent: true,
          showProductHistory: true,
          showSlider: true,
        },
        navigationBarSettings: {
          allowedNavigationSize: 3,
        },
      },
    }),
    setStyling: () => {},
    normalizeLanguages: () => {
      return CustomizeServiceStub.currentTheme.toPromise();
    },
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
      declarations: [FooterComponent, TranslatePipe],
      imports: [
        HttpClientTestingModule,
        MarkdownModule.forRoot(),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          },
        }),
      ],
      providers: [TranslateService, { provide: CustomizeService, useValue: CustomizeServiceStub }],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
