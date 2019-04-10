import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RoutingPath} from "../common/models/routing-path.model";
import {LoginComponent} from "./login/login.component";

const routes: Routes = [
  {
    path: RoutingPath.LOGIN,
    component: LoginComponent,
  }
  // {
  //   path: RoutingPath.RESULT,
  //   component: ResultPageComponent,
  // },
  // {
  //   path: RoutingPath.BANK_OFFERED,
  //   component: BankOfferedComponent,
  // },
  // {
  //   path: RoutingPath.TAN_CONFIRMATION,
  //   component: TanConfirmationComponent,
  // },
  // {
  //   path: RoutingPath.SELECT_SCA,
  //   component: ScaSelectionComponent
  // }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AisRoutingModule {
}
