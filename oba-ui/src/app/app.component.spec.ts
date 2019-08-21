import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {RouterOutlet} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

import {AppComponent} from './app.component';
import {URL_PARAMS_PROVIDER} from './common/constants/constants';
import {ApiConfiguration} from "./api/api-configuration";
import {NgHttpLoaderModule} from "ng-http-loader";

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports: [
        RouterTestingModule,
        NgHttpLoaderModule.forRoot()
      ],
      providers: [
        ApiConfiguration,
        {provide: URL_PARAMS_PROVIDER, useValue: {}}
      ]
    }).compileComponents();
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('Should include the < router-outlet >', async(() => {
    fixture.detectChanges();
    const debugElement = fixture.debugElement.query(By.directive(RouterOutlet));
    expect(debugElement).not.toBeNull();
  }));

});
