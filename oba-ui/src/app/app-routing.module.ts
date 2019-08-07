import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ConsentsComponent} from './consents/consents.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {AuthGuard} from './common/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'consents',
        component: ConsentsComponent
      }
    ]
  },
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
    redirectTo: '/login'
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
