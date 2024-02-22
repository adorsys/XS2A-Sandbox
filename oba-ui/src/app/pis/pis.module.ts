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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NotFoundModule } from '../not-found/not-found.module';
import { ConfirmPaymentComponent } from './confirm-payment/confirm-payment.component';
import { LoginComponent } from './login/login.component';
import { PaymentDetailsComponent } from './payment-details/payment-details.component';
import { PisRoutingModule } from './pis-routing.module';
import { ResultPageComponent } from './result-page/result-page.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { TanConfirmationComponent } from './tan-confirmation/tan-confirmation.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

@NgModule({
  declarations: [
    LoginComponent,
    TanConfirmationComponent,
    ResultPageComponent,
    SelectScaComponent,
    PaymentDetailsComponent,
    ConfirmPaymentComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PisRoutingModule,
    NotFoundModule,
    NgMultiSelectDropDownModule,
  ],
})
export class PisModule {}
