import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PostmanTestingComponent} from './postman-testing.component';
import {Pipe, PipeTransform} from '@angular/core';
import {MarkdownModule} from "ngx-markdown";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpLoaderFactory, LanguageService} from "../../../../services/language.service";
import {HttpClient} from "@angular/common/http";

describe('PostmanTestingComponent', () => {
  let component: PostmanTestingComponent;
  let fixture: ComponentFixture<PostmanTestingComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
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
      ],
      declarations: [
        PostmanTestingComponent,
        TranslatePipe
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PostmanTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
