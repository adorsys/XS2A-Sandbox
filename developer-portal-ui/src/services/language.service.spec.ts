import { TestBed, inject } from '@angular/core/testing';

import {HttpLoaderFactory, LanguageService} from './language.service';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('LanguageService', () => {
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
  });

  it('should be created (not all)', inject([LanguageService], (service: LanguageService) => {
    expect(service).toBeTruthy();
  }));
});
