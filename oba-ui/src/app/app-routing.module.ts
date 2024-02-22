/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { InjectionToken, NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './common/guards/auth.guard';
import { AccountDetailsComponent } from './oba/accounts/account-details/account-details.component';
import { AccountsComponent } from './oba/accounts/accounts.component';
import { ConfirmPasswordComponent } from './oba/confirm-password/confirm-password.component';
import { DashboardComponent } from './oba/dashboard/dashboard.component';
import { LoginComponent } from './oba/login/login.component';
import { ResetPasswordComponent } from './oba/reset-password/reset-password.component';
import { PeriodicPaymentsComponent } from './oba/periodic-payments/periodic-payments.component';
import { UserProfileComponent } from './oba/user-profile/user-profile.component';
import { UserProfileUpdateComponent } from './oba/user-profile-update/user-profile-update.component';
import { VerifyEmailComponent } from './oba/verify-email/verify-email.component';
import { ConsentAisComponent } from './oba/consents/ais/consent-ais.component';
import { ConsentPiisComponent } from './oba/consents/piis/consent-piis.component';

const externalUrlProvider = new InjectionToken('externalUrlRedirectResolver');

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
  },
  {
    path: 'confirm-password',
    component: ConfirmPasswordComponent,
  },
  {
    path: 'email-confirmation',
    children: [
      {
        path: 'success',
        component: VerifyEmailComponent,
      },
      {
        path: 'fail',
        component: VerifyEmailComponent,
      },
    ],
  },
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'accounts',
      },
      {
        path: 'consents',
        children: [
          {
            path: 'ais',
            component: ConsentAisComponent,
          },
          {
            path: 'piis',
            component: ConsentPiisComponent,
          },
        ],
      },
      {
        path: 'accounts',
        component: AccountsComponent,
      },
      {
        path: 'profile',
        component: UserProfileComponent,
      },
      {
        path: 'profile-edit',
        component: UserProfileUpdateComponent,
      },
      {
        path: 'accounts/:id',
        component: AccountDetailsComponent,
      },
      {
        path: 'periodic-payments',
        component: PeriodicPaymentsComponent,
      },
    ],
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'account-information',
    loadChildren: () => import('./ais/ais.module').then((m) => m.AisModule),
  },
  {
    path: 'payment-initiation',
    loadChildren: () => import('./pis/pis.module').then((m) => m.PisModule),
  },
  {
    path: 'payment-cancellation',
    loadChildren: () =>
      import('./payment-cancellation/payment-cancellation.module').then(
        (m) => m.PaymentCancellationModule
      ),
  },
  {
    path: 'externalRedirect',
    canActivate: [externalUrlProvider],
    component: LoginComponent,
  },

  {
    path: '**',
    redirectTo: '/login',
  },
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forRoot(routes, {})],
  exports: [RouterModule],
  providers: [
    {
      provide: externalUrlProvider,
      useValue: (route: ActivatedRouteSnapshot) => {
        const externalUrl = route.paramMap.get('externalUrl');
        window.open(externalUrl, '_self');
      },
    },
  ],
})
export class AppRoutingModule {}
