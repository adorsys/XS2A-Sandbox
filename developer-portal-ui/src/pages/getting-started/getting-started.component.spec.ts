import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GettingStartedComponent } from './getting-started.component';
import { Pipe, PipeTransform } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';
import { CodeAreaComponent } from '../../custom-elements/code-area/code-area.component';
import { DataService } from '../../services/data.service';
import { ToastrService } from 'ngx-toastr';

describe('GettingStartedComponent', () => {
  let component: GettingStartedComponent;
  let fixture: ComponentFixture<GettingStartedComponent>;

  const CustomizeServiceStub = {
    getTheme: () => {
      return {
        globalSettings: {
          logo: '../assets/UI/Logo_XS2ASandbox.png',
          cssVariables: {
            colorPrimary: '#054f72',
            colorSecondary: '#eed52f',
            fontFamily: 'Arial, sans-serif',
            headerBG: '#ffffff',
            headerFontColor: '#000000',
            footerBG: '#054f72',
            footerFontColor: '#ffffff',
          },
          facebook: 'https://www.facebook.com/adorsysGmbH/',
          linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
        },
        contactInfo: {
          img: 'Rene.png',
          name: 'René Pongratz',
          position: 'Software Architect & Expert for API Management',
          email: 'psd2@adorsys.de',
        },
        officesInfo: [
          {
            city: 'Nürnberg',
            company: 'adorsys GmbH & Co. KG',
            addressFirstLine: 'Fürther Str. 246a, Gebäude 32 im 4.OG',
            addressSecondLine: '90429 Nürnberg',
            phone: '+49(0)911 360698-0',
            email: 'psd2@adorsys.de',
          },
          {
            city: 'Frankfurt',
            company: 'adorsys GmbH & Co. KG',
            addressFirstLine: 'Frankfurter Straße 63 - 69',
            addressSecondLine: '65760 Eschborn',
            email: 'frankfurt@adorsys.de',
            facebook: 'https://www.facebook.com/adorsysGmbH/',
            linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
          },
        ],
      };
    },
  };

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  @Pipe({ name: 'prettyJson' })
  class PrettyJsonPipe implements PipeTransform {
    transform(value) {
      return JSON.stringify(value, null, 4);
    }
  }

  const ToastrServiceStub = {};

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GettingStartedComponent,
        TranslatePipe,
        CodeAreaComponent,
        PrettyJsonPipe,
      ],
      providers: [
        { provide: CustomizeService, useValue: CustomizeServiceStub },
        DataService,
        { provide: ToastrService, useValue: ToastrServiceStub },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GettingStartedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should export theme', () => {
    expect(component.exportTheme()).toBeTruthy();
  });

  it('should set settings and default theme in ngOnInit', () => {
    component.ngOnInit();

    expect(component.defaultTheme).not.toBeUndefined();
    expect(component.settings).not.toBeUndefined();
  });
});
