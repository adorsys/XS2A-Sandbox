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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PaymentDetailsComponent } from './payment-details.component';

describe('PaymentDetailsComponent', () => {
  let component: PaymentDetailsComponent;
  let fixture: ComponentFixture<PaymentDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [PaymentDetailsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return empty when no auth response for accounts', () => {
    component.authResponse = null;
    const result = component.totalAmount;
    expect(result).toBe(0);
  });

  it('should return 0 when auth response for accounts has no payment', () => {
    component.authResponse = {
      payment: null,
    };
    const result = component.totalAmount;
    expect(result).toBe(0);
  });

  it('should return totalAmount when auth response for accounts has payments', () => {
    component.authResponse = {
      payment: {
        targets: [
          {
            instructedAmount: {
              amount: 15,
            },
          },
          {
            instructedAmount: {
              amount: 110,
            },
          },
        ],
      },
    };
    const result = component.totalAmount;
    expect(result).toBe(125);
  });
});
