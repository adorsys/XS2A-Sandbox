/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { Component, Input, Pipe, PipeTransform } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';

import { JsonService } from '../../../../../services/json.service';
import { LineCommandComponent } from '../../../../common/line-command/line-command.component';
import { VopBulkVerificationComponent } from './vop-bulk-verification.component';

describe('VopBulkVerificationComponent', () => {
  let component: VopBulkVerificationComponent;
  let fixture: ComponentFixture<VopBulkVerificationComponent>;
  let mockJsonService: jasmine.SpyObj<JsonService>;

  @Component({
  standalone: false,
    selector: 'app-play-wth-data',
    template: '',
  })
  class MockPlayWithDataComponent {
    @Input() method: string;
    @Input() headers: object;
    @Input() url: string;
    @Input() body: any;
    @Input() bulkVerificationFlag: boolean;
  }

  @Component({
    standalone: false,
    selector: 'app-code-area',
    template: '',
  })
  class MockCodeAreaComponent {
    @Input() json: any;
  }

  @Pipe({ standalone: false, name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[tmp.length - 1];
    }
  }

  beforeEach(
    waitForAsync(() => {
      const jsonServiceSpy = jasmine.createSpyObj('JsonService', ['getPreparedJsonData'], {
        jsonLinks: {
          submitBulkVerification: 'test-bulk-link'
        }
      });

      TestBed.configureTestingModule({
        declarations: [
          VopBulkVerificationComponent,
          MockPlayWithDataComponent,
          MockCodeAreaComponent,
          TranslatePipe,
          LineCommandComponent
        ],
        providers: [
          { provide: JsonService, useValue: jsonServiceSpy }
        ]
      }).compileComponents();

      mockJsonService = TestBed.inject(JsonService) as jasmine.SpyObj<JsonService>;
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(VopBulkVerificationComponent);
    component = fixture.componentInstance;
    
    // Mock the JSON service response
    const mockJsonData = {
      bulkVerificationOfParty: [
        {
          accountIdentifier: 'DE89370400440532013000',
          name: 'John Doe'
        },
        {
          accountIdentifier: 'DE89370400440532013001',
          name: 'Jane Smith'
        }
      ]
    };
    mockJsonService.getPreparedJsonData.and.returnValue(of(mockJsonData));
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct initial values', () => {
    expect(component.activeSegment).toBe('documentation');
    expect(component.headers).toEqual({
    });
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

  it('should load JSON data on init', () => {
    expect(mockJsonService.getPreparedJsonData).toHaveBeenCalledWith('test-bulk-link');
    expect(component.body).toBeDefined();
  });
});