import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctPaymentCancellationPostComponent } from './rdct-payment-cancellation-post.component';
import { Component, Input, Pipe, PipeTransform } from '@angular/core';
import { LineCommandComponent } from '../../../../../custom-elements/line-command/line-command.component';
import { CodeAreaComponent } from '../../../../../custom-elements/code-area/code-area.component';
import { JsonService } from '../../../../../services/json.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../../../services/language.service';
import { HttpClient } from '@angular/common/http';
import { DataService } from '../../../../../services/data.service';
import { ToastrService } from 'ngx-toastr';

describe('RdctPaymentCancellationPostComponent', () => {
  let component: RdctPaymentCancellationPostComponent;
  let fixture: ComponentFixture<RdctPaymentCancellationPostComponent>;
  let jsonService: JsonService;

  @Component({
    selector: 'app-play-wth-data',
    template: '',
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() body: object;
    @Input() paymentServiceFlag: boolean;
    @Input() paymentProductFlag: boolean;
    @Input() fieldsToCopy: string[];
  }

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
        RdctPaymentCancellationPostComponent,
        MockPlayWithDataComponent,
        TranslatePipe,
        PrettyJsonPipe,
        LineCommandComponent,
        CodeAreaComponent,
        CodeAreaComponent,
      ],
      providers: [
        TranslateService,
        DataService,
        { provide: ToastrService, useValue: ToastrServiceStub },
      ],
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
    }).compileComponents();
  }));

  beforeEach(() => {
    jsonService = TestBed.get(JsonService);
    spyOn(jsonService, 'getPreparedJsonData').and.returnValue(of('body'));
    fixture = TestBed.createComponent(RdctPaymentCancellationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be right headers', () => {
    const headers: object = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'TPP-Explicit-Authorisation-Preferred': 'true',
      'PSU-ID': 'YOUR_USER_LOGIN',
      'PSU-IP-Address': '1.1.1.1',
      'TPP-Redirect-Preferred': 'true',
      'TPP-Redirect-URI': 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
      'TPP-Nok-Redirect-URI': 'https://www.google.com',
    };
    expect(typeof component.headers).toBe('object');
    for (const key in component.headers) {
      if (component.headers.hasOwnProperty(key)) {
        expect(headers.hasOwnProperty(key)).toBeTruthy();
        expect(headers[key]).toBe(component.headers[key]);
      }
    }
  });

  it('should change segment', () => {
    expect(component.activeSegment).toBe('documentation');

    component.changeSegment('play-data');
    expect(component.activeSegment).toBe('play-data');

    component.changeSegment('documentation');
    expect(component.activeSegment).toBe('documentation');

    component.changeSegment('wrong-segment');
    expect(component.activeSegment).not.toBe('wrong-segment');
  });

  it('should be body', () => {
    expect(component.body).not.toBeUndefined();
  });
});
