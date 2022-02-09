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
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { InfoService } from '../../common/info/info.service';
import { PeriodicPaymentsComponent } from './periodic-payments.component';
import { ClipboardModule } from 'ngx-clipboard';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { PaymentTO } from '../../api/models/payment-to';
import { of } from 'rxjs';

describe('PeriodicPaymentsComponent', () => {
  let component: PeriodicPaymentsComponent;
  let fixture: ComponentFixture<PeriodicPaymentsComponent>;
  let inforService: InfoService;
  let onlineBankingService: OnlineBankingService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterTestingModule,
          InfoModule,
          ClipboardModule,
        ],
        declarations: [PeriodicPaymentsComponent],
        providers: [InfoService, OnlineBankingService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicPaymentsComponent);
    component = fixture.componentInstance;
    inforService = TestBed.inject(InfoService);
    onlineBankingService = TestBed.inject(OnlineBankingService);
    fixture.detectChanges();
  });

  /* it('should create', () => {
        expect(component).toBeTruthy(); //TODO Fix me!
    });*/

  /* it('should get the Periodic Payments', () => {
        let payments: PaymentTO = {
        }
        let periodicSpy = spyOn(onlineBankingService, 'getPeriodicPaymentsPaged').and.returnValue(of({payments: payments}));
        component.getPeriodicPaymentsPaged(0,1);
        expect(periodicSpy).toHaveBeenCalled();
    });*/
});
