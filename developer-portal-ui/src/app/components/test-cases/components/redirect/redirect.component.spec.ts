import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RedirectComponent} from './redirect.component';
import {Pipe, PipeTransform} from '@angular/core';
import {NgxImageZoomModule} from 'ngx-image-zoom';
import {MarkdownModule} from "ngx-markdown";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpLoaderFactory, LanguageService} from "../../../../services/language.service";
import {HttpClient} from "@angular/common/http";

describe('RedirectComponent', () => {
  let component: RedirectComponent;
  let fixture: ComponentFixture<RedirectComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectComponent, TranslatePipe],
      imports: [
        NgxImageZoomModule.forRoot(),
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
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
