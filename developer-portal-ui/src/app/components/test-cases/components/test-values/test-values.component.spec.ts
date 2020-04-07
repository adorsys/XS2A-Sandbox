import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestValuesComponent } from './test-values.component';
import { Pipe, PipeTransform } from '@angular/core';
import { MarkdownModule } from 'ngx-markdown';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import {
  HttpLoaderFactory,
  LanguageService,
} from '../../../../services/language.service';
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

  beforeEach(async(() => {
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
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestValuesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
