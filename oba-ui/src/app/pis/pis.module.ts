import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PisRoutingModule} from './pis-routing.module';
import {LoginComponent} from './login/login.component';
import {TanConfirmationComponent} from './tan-confirmation/tan-confirmation.component';
import {ResultPageComponent} from './result-page/result-page.component';
import {SelectScaComponent} from './select-sca/select-sca.component';
import {PaymentDetailsComponent} from './payment-details/payment-details.component';
import {ConfirmPaymentComponent} from './confirm-payment/confirm-payment.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { NotFoundModule } from '../not-found/not-found.module';

@NgModule({
  declarations: [
    LoginComponent,
    TanConfirmationComponent,
    ResultPageComponent,
    SelectScaComponent,
    PaymentDetailsComponent,
    ConfirmPaymentComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PisRoutingModule,
    NotFoundModule
  ]
})
export class PisModule {
}
