/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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

import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ConvertBalancePipe } from './convertBalance.pipe';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

@Component({
  template: '<div> {{ balance | convertBalance }} </div>',
})
export class ConvertBalancePipeHostComponent {
  balance: number;
}

describe('ConvertBalancePipe inside a Component', () => {
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvertBalancePipe, ConvertBalancePipeHostComponent],
    }).compileComponents();
  }));

  let fixture: ComponentFixture<ConvertBalancePipeHostComponent>;
  let debugElement: DebugElement;
  let component: ConvertBalancePipeHostComponent;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvertBalancePipeHostComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(fixture).toBeTruthy();
  });

  it('should Display 0,44', () => {
    component.balance = 0.44;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('0,44');
  });

  it('should Display 10.000.000,00', () => {
    component.balance = 10000000;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('10.000.000,00');
  });

  it('should Display 10.000.000,50', () => {
    component.balance = 10000000.5;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('10.000.000,50');
  });
});
