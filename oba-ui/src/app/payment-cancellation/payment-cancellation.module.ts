import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PaymentCancellationRoutingModule} from './payment-cancellation-routing.module';
import {TanConfirmationComponent} from './tan-confirmation/tan-confirmation.component';
import {LoginComponent} from './login/login.component';
import {ConfirmCancellationComponent} from './confirm-cancellation/confirm-cancellation.component';
import {PaymentDetailsComponent} from './payment-details/payment-details.component';
import {SelectScaComponent} from './select-sca/select-sca.component';
import {ResultPageComponent} from './result-page/result-page.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { NotFoundModule } from '../not-found/not-found.module';

@NgModule({
  declarations: [
    TanConfirmationComponent,
    LoginComponent, ConfirmCancellationComponent,
    PaymentDetailsComponent,
    SelectScaComponent,
    ResultPageComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    PaymentCancellationRoutingModule,
    NotFoundModule
  ]
})
export class PaymentCancellationModule {
}
