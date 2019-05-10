import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {RoutingPath} from "../common/models/routing-path.model";
import {LoginComponent} from "./login/login.component";
import {SelectScaComponent} from "./select-sca/select-sca.component";
import {TanConfirmationComponent} from "./tan-confirmation/tan-confirmation.component";
import {ResultPageComponent} from "./result-page/result-page.component";
import {ConfirmPaymentComponent} from "./confirm-payment/confirm-payment.component";
import {NotFoundComponent} from "../not-found/not-found.component";

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
  },
  {
    path: '**',
    component: NotFoundComponent
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PisRoutingModule { }
