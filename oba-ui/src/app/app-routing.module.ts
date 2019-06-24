import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

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
  {
    path: '**',
    redirectTo: '/account-information/login'
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
