import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CustomPageComponent} from './custom-page.component';
import {MarkdownModule} from "ngx-markdown";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpLoaderFactory, LanguageService} from "../../services/language.service";
import {HttpClient} from "@angular/common/http";
import {RouterTestingModule} from "@angular/router/testing";

describe('CustomPageComponent', () => {
  let component: CustomPageComponent;
  let fixture: ComponentFixture<CustomPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CustomPageComponent],
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
        })],
      providers: [
        LanguageService,
        TranslateService
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
