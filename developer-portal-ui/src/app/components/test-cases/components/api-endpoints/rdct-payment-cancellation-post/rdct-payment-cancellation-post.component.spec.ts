/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RdctPaymentCancellationPostComponent } from './rdct-payment-cancellation-post.component';
import { Component, Input, Pipe, PipeTransform } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../../../services/language.service';
import { HttpClient } from '@angular/common/http';
import { DataService } from '../../../../../services/data.service';
import { ToastrService } from 'ngx-toastr';
import { LineCommandComponent } from '../../../../common/line-command/line-command.component';
import { CodeAreaComponent } from '../../../../common/code-area/code-area.component';
import { JSON_SPACING } from '../../../../common/constant/constants';

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
      return JSON.stringify(value, null, JSON_SPACING);
    }
  }

  const ToastrServiceStub = {};

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          RdctPaymentCancellationPostComponent,
          MockPlayWithDataComponent,
          TranslatePipe,
          PrettyJsonPipe,
          LineCommandComponent,
          CodeAreaComponent,
        ],
        providers: [TranslateService, DataService, { provide: ToastrService, useValue: ToastrServiceStub }],
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
    })
  );

  beforeEach(() => {
    jsonService = TestBed.inject(JsonService);
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

  it('should be body', () => {
    expect(component.body).not.toBeUndefined();
  });
});
