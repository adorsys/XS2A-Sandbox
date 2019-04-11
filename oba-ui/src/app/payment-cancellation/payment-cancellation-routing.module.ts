import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {RoutingPath} from "../common/models/routing-path.model";
import {LoginComponent} from "../pis/login/login.component";
import {ConfirmPaymentComponent} from "../pis/confirm-payment/confirm-payment.component";
import {SelectScaComponent} from "../pis/select-sca/select-sca.component";
import {TanConfirmationComponent} from "../pis/tan-confirmation/tan-confirmation.component";
import {ResultPageComponent} from "../pis/result-page/result-page.component";

const routes: Routes = [
  {
    path: RoutingPath.LOGIN,
    component: LoginComponent,
  },
  {
    path: RoutingPath.CONFIRM_PAYMENT,
    component: ConfirmPaymentComponent,
  },
  {
    path: RoutingPath.SELECT_SCA,
    component: SelectScaComponent
  },
  {
    path: RoutingPath.TAN_CONFIRMATION,
    component: TanConfirmationComponent,
  },
  {
    path: RoutingPath.RESULT,
    component: ResultPageComponent,
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaymentCancellationRoutingModule { }
