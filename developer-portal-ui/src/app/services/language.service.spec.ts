/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { TestBed } from '@angular/core/testing';

import { HttpLoaderFactory, LanguageService } from './language.service';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LocalStorageService } from './local-storage.service';

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
            deps: [HttpClient],
          },
        }),
      ],
      providers: [LanguageService, TranslateService],
    });
    service = TestBed.inject(LanguageService);
    translate = TestBed.inject(TranslateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('default translation should be en', () => {
    LocalStorageService.remove('userLanguage');
    service.initializeTranslation();
    expect(translate.getDefaultLang()).toEqual('en');
    expect(translate.currentLang).toEqual('en');
  });

  it('should change lang', () => {
    service.setLanguage('ua');
    expect(translate.currentLang).toEqual('ua');
    expect(service.getLang()).toEqual('ua');
  });
});
