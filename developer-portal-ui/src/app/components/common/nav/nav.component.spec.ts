import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavComponent} from './nav.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpLoaderFactory, LanguageService} from "../../../services/language.service";
import {TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Pipe, PipeTransform} from "@angular/core";
import {DataService} from "../../../services/data.service";

describe('NavComponent', () => {
  let component: NavComponent;
  let fixture: ComponentFixture<NavComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  const DataServiceStub = {
    setRouterUrl: (val: string) => {
    },
    getRouterUrl: () => '',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavComponent,
        TranslatePipe
      ],
      imports: [
        RouterTestingModule,
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
        TranslateService,
        {provide: DataService, useValue: DataServiceStub},
      ],

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change language', () => {
    component.supportedLanguages = ['en'];
    expect(component.getLangCollapsed()).toBeTruthy();
    component.changeLang('ua');
    expect(component.language).toEqual('ua');
  });

  it('should collapse', () => {
    let prevCollapseStatus;
    for (let i = 0; i < 2; i++) {
      prevCollapseStatus = component.getLangCollapsed();
      component.collapseThis();
      expect(component.getLangCollapsed).not.toEqual(prevCollapseStatus);
    }
  });
});
