import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RoutingPath} from "../common/models/routing-path.model";
import {LoginComponent} from "./login/login.component";
import {ConfirmCancellationComponent} from "./confirm-cancellation/confirm-cancellation.component";
import {SelectScaComponent} from "./select-sca/select-sca.component";
import {TanConfirmationComponent} from "./tan-confirmation/tan-confirmation.component";
import {ResultPageComponent} from "./result-page/result-page.component";

const routes: Routes = [
  {
    path: RoutingPath.LOGIN,
    component: LoginComponent,
  },
  {
    path: RoutingPath.CONFIRM_PAYMENT,
    component: ConfirmCancellationComponent,
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
export class PaymentCancellationRoutingModule {
}
