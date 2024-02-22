/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CodeAreaComponent } from './code-area.component';
import { Pipe, PipeTransform } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { DataService } from '../../../services/data.service';
import { JSON_SPACING } from '../constant/constants';

describe('CodeAreaComponent', () => {
  let component: CodeAreaComponent;
  let fixture: ComponentFixture<CodeAreaComponent>;

  const transformPipeValue = 3;

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[transformPipeValue];
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
        declarations: [CodeAreaComponent, TranslatePipe, PrettyJsonPipe],
        providers: [DataService, { provide: ToastrService, useValue: ToastrServiceStub }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CodeAreaComponent);
    component = fixture.componentInstance;
    component.id = 'Collapse-test';
    component.json = {
      endToEndIdentification: 'WBG-123456789',
      debtorAccount: {
        currency: 'EUR',
        iban: 'YOUR_USER_IBAN',
      },
      instructedAmount: {
        currency: 'EUR',
        amount: '20.00',
      },
      creditorAccount: {
        currency: 'EUR',
        iban: 'DE15500105172295759744',
      },
      creditorAgent: 'AAAADEBBXXX',
      creditorName: 'WBG',
      creditorAddress: {
        buildingNumber: '56',
        city: 'Nürnberg',
        country: 'DE',
        postalCode: '90543',
        street: 'WBG Straße',
      },
      remittanceInformationUnstructured: 'Ref. Number WBG-1222',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should collapse code area', () => {
    let collapsedArea = document.getElementById('Collapse-test').style.maxHeight;
    component.collapseThis('Collapse-test');

    expect(collapsedArea).not.toEqual(document.getElementById('Collapse-test').style.maxHeight);
    expect(component.shown).toBeTruthy();

    collapsedArea = document.getElementById('Collapse-test').style.maxHeight;
    component.collapseThis('Collapse-test');

    expect(collapsedArea).not.toEqual(document.getElementById('Collapse-test').style.maxHeight);
    expect(component.shown).toBeFalsy();
  });
});
