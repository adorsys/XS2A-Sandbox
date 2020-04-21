import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctConsentPOSTComponent } from './rdct-consent-post.component';
import { Component, Input, Pipe, PipeTransform } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DataService } from '../../../../../services/data.service';
import { ToastrService } from 'ngx-toastr';
import { NgHttpLoaderModule, SpinnerVisibilityService } from 'ng-http-loader';
import { LineCommandComponent } from '../../../../common/line-command/line-command.component';
import { CodeAreaComponent } from '../../../../common/code-area/code-area.component';
import { JSON_SPACING } from '../../../../common/constant/constants';

describe('RdctConsentPOSTComponent', () => {
  let component: RdctConsentPOSTComponent;
  let fixture: ComponentFixture<RdctConsentPOSTComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: '',
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() body: object;
    @Input() fieldsToCopy: string[];
    @Input() dateFromFlag: boolean;
    @Input() consentTypes: string[];
    @Input() consentBodies: JSON[];
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
      return JSON.stringify(value, null, JSON_SPACING);
    }
  }

  const ToastrServiceStub = {};

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        RdctConsentPOSTComponent,
        TranslatePipe,
        PrettyJsonPipe,
        MockPlayWithDataComponent,
        LineCommandComponent,
        CodeAreaComponent,
      ],
      imports: [HttpClientTestingModule, NgHttpLoaderModule.forRoot()],
      providers: [DataService, { provide: ToastrService, useValue: ToastrServiceStub }, SpinnerVisibilityService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctConsentPOSTComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be right headers', () => {
    const headers: object = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'TPP-Explicit-Authorisation-Preferred': 'false',
      'PSU-ID': 'YOUR_USER_LOGIN',
      'PSU-IP-Address': '1.1.1.1',
      'TPP-Redirect-Preferred': 'true',
      'TPP-Redirect-URI': null,
      'TPP-Nok-Redirect-URI': null,
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
});
