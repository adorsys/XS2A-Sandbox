import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NotFoundComponent} from "./not-found/not-found.component";
// import {LoginComponent} from "./ais/login/login.component";

export const routes: Routes = [
  {
    path: 'account-information',
    loadChildren: './ais/ais.module#AisModule'
  },
  {
    path: 'payment-initiation',
    loadChildren: './pis/pis.module#PisModule'
  },
  {
    path: 'payment-cancellation',
    loadChildren: './payment-cancellation/payment-cancellation.module#PaymentCancellationModule'
  },
  // {
  //   path: '',
  //   redirectTo: 'login',
  //   pathMatch: 'full'
  // },
  // {
  //   path: RoutingPath.LOGIN,
  //   component: LoginComponent,
  // },
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
  //   component: ScaSelectionComponent,
  // },
  {
    path: '**',
    component: NotFoundComponent
  }

];

@NgModule({
  declarations: [],
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})

export class AppRoutingModule {
}
