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

import { LineCommandComponent } from '../../../../common/line-command/line-command.component';
import { VopReadBulkStatusComponent } from './vop-read-bulk-status.component';

describe('VopReadBulkStatusComponent', () => {
  let component: VopReadBulkStatusComponent;
  let fixture: ComponentFixture<VopReadBulkStatusComponent>;

  @Component({
  standalone: false,
    selector: 'app-play-wth-data',
    template: '',
  })
  class MockPlayWithDataComponent {
    @Input() method: string;
    @Input() headers: object;
    @Input() url: string;
    @Input() variablePathEnd: string;
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
      TestBed.configureTestingModule({
        declarations: [
          VopReadBulkStatusComponent,
          MockPlayWithDataComponent,
          TranslatePipe,
          LineCommandComponent
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(VopReadBulkStatusComponent);
    component = fixture.componentInstance;
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
});