import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './common/guards/auth.guard';
import { AccountDetailsComponent } from './oba/accounts/account-details/account-details.component';
import { AccountsComponent } from './oba/accounts/accounts.component';
import { ConfirmPasswordComponent } from './oba/confirm-password/confirm-password.component';
import { ConsentsComponent } from './oba/consents/consents.component';
import { DashboardComponent } from './oba/dashboard/dashboard.component';
import { LoginComponent } from './oba/login/login.component';
import { ResetPasswordComponent } from './oba/reset-password/reset-password.component';

export const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'reset-password',
        component: ResetPasswordComponent
    },
    {
        path: 'confirm-password',
        component: ConfirmPasswordComponent
    },
    {
        path: '',
        component: DashboardComponent,
        canActivate: [AuthGuard],
        children: [
            {
                path: 'consents',
                component: ConsentsComponent
            },
            {
                path: 'accounts',
                component: AccountsComponent
            },
            {
                path: 'accounts/:id',
                component: AccountDetailsComponent
            },
        ]
    },
    {
        path: 'account-information',
        loadChildren: () => import('./ais/ais.module').then(m => m.AisModule)
    },
    {
        path: 'payment-initiation',
        loadChildren: () => import('./pis/pis.module').then(m => m.PisModule)
    },
    {
        path: 'payment-cancellation',
        loadChildren: () => import('./payment-cancellation/payment-cancellation.module').then(m => m.PaymentCancellationModule)
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
