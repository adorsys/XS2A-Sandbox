import { TestBed, inject } from '@angular/core/testing';

import {HttpLoaderFactory, LanguageService} from './language.service';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('LanguageService', () => {
  let translate: TranslateService;
  let service: LanguageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        })
      ],
      providers: [
        LanguageService,
        TranslateService
      ]
    });
    service = TestBed.get(LanguageService);
    translate = TestBed.get(TranslateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('default translation should be \'en\', and there should be 3 another langs', () => {
    service.initializeTranslation();
    expect(translate.getDefaultLang()).toEqual('en');
    expect(translate.currentLang).toEqual('en');
    expect(translate.getLangs()).toContain('en');
    expect(translate.getLangs()).toContain('de');
    expect(translate.getLangs()).toContain('es');
    expect(translate.getLangs()).toContain('ua');
  });

  it('should change lang', () => {
    service.setLang('ua');
    expect(translate.currentLang).toEqual('ua');
    expect(service.getLang()).toEqual('ua');
  });
});
