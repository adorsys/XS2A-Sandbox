import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NotFoundModule } from '../not-found/not-found.module';
import { ConfirmCancellationComponent } from './confirm-cancellation/confirm-cancellation.component';
import { LoginComponent } from './login/login.component';
import { PaymentCancellationRoutingModule } from './payment-cancellation-routing.module';
import { PaymentDetailsComponent } from './payment-details/payment-details.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { TanConfirmationComponent } from './tan-confirmation/tan-confirmation.component';

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
