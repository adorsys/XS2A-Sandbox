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
import {NotFoundComponent} from "../not-found/not-found.component";

@NgModule({
  declarations: [
    LoginComponent,
    TanConfirmationComponent,
    ResultPageComponent,
    SelectScaComponent,
    PaymentDetailsComponent,
    ConfirmPaymentComponent,
    NotFoundComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PisRoutingModule
  ]
})
export class PisModule {
}
