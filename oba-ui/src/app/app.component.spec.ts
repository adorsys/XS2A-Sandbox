import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgHttpLoaderModule } from 'ng-http-loader';
import { of } from 'rxjs';
import {Title} from "@angular/platform-browser";
import { ApiConfiguration } from './api/api-configuration';
import { AppComponent } from './app.component';
import { URL_PARAMS_PROVIDER } from './common/constants/constants';
import { CustomizeService } from './common/services/customize.service';
import { ShareDataService } from './common/services/share-data.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let customizeService: CustomizeService;
  let shareDataService: ShareDataService;
  let titleService: Title;
  const CustomizeServiceStub = {
    isCustom: () => false,
    setUserTheme: () => {},
    getJSON: () => {
      return of({
        globalSettings: {
          logo: '../assets/UI/Logo_XS2ASandbox.png',
          cssVariables: {
            colorPrimary: '#054f72',
            fontFamily: 'Arial, sans-serif',
            headerBG: '#ffffff',
            headerFontColor: '#000000'
          }
        }
      });
    },
    getLogo: () => '../assets/UI/Logo_XS2ASandbox.png',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports: [
        RouterTestingModule,
        NgHttpLoaderModule.forRoot(),
      ],
      providers: [
        ApiConfiguration,
        {provide: URL_PARAMS_PROVIDER, useValue: {}},
        {provide: CustomizeService, useValue: CustomizeServiceStub},
          ShareDataService, Title
      ]
    })
    .compileComponents()
    .then(() => {
      customizeService = TestBed.get(CustomizeService);
    });
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    titleService = TestBed.get(Title);
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

  it('should set global settings in ngOnInit', () => {
    const getGlobalSettingsSpy = spyOn(
      customizeService,
      'getJSON'
    ).and.callThrough();

    component.ngOnInit();

    expect(getGlobalSettingsSpy).toHaveBeenCalled();
    expect(component.globalSettings).not.toBeUndefined();
  });


  it('should check the Url', () => {
      let mockUrl = '/login';
      component.checkUrl();
      expect(mockUrl).toEqual('/login');
  });

  it('should check the Url Bank', () => {
    let mockUrl = '/bank-offered';
      component.checkUrlbank();
      expect(mockUrl).toEqual('/bank-offered');
  });
});
