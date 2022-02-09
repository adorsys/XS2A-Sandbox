/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TestValuesComponent } from './test-values.component';
import { Pipe, PipeTransform } from '@angular/core';
import { MarkdownModule } from 'ngx-markdown';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpLoaderFactory, LanguageService } from '../../../../services/language.service';
import { HttpClient } from '@angular/common/http';
import { DataService } from '../../../../services/data.service';
import { CertificateService } from '../../../../services/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

describe('TestValuesComponent', () => {
  let component: TestValuesComponent;
  let fixture: ComponentFixture<TestValuesComponent>;
  const ToastrServiceStub = {};

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          BrowserModule,
          FormsModule,
          MarkdownModule.forRoot(),
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
          LanguageService,
          TranslateService,
          DataService,
          { provide: ToastrService, useValue: ToastrServiceStub },
          CertificateService,
        ],
        declarations: [TestValuesComponent, TranslatePipe],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TestValuesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
