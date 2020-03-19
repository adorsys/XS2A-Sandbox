import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HomeComponent} from './home.component';
import {Pipe, PipeTransform} from '@angular/core';
import {CustomizeService} from '../../services/customize.service';
import {MarkdownModule} from "ngx-markdown";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {HttpLoaderFactory, LanguageService} from "../../services/language.service";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {of} from "rxjs";

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  const CustomizeServiceStub = {
    isCustom: () => false,
    getJSON: () => of({
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
          linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
        },
      ],
      pagesSettings: {
        contactPageSettings: {
          showContactCard: true,
          showQuestionsComponent: true
        },
        homePageSettings: {
          showQuestionsComponent: true,
          showProductHistory: true,
          showSlider: true
        }
      }
    }).toPromise()
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
      declarations: [HomeComponent, TranslatePipe],
      imports: [
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
        {provide: CustomizeService, useValue: CustomizeServiceStub},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check is it today', () => {
    expect(component.checkTodayDay(+new Date())).toBeTruthy();
    expect(component.checkTodayDay(0)).toBeFalsy();
  });

  it('should set product history', () => {
    component.setProductHistory();

    expect(
      component.productHistory[component.productHistory.length - 1].date >
      component.today
    ).toBeFalsy();
    expect(component.productHistory[0].date > component.today).toBeFalsy();
  });
});
