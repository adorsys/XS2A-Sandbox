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
