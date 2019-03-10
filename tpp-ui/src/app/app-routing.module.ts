import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AccountDetailComponent} from './components/account-detail/account-detail.component';
import {AccountListComponent} from './components/account-list/account-list.component';
import {AccountComponent} from './components/account/account.component';
import {LoginComponent} from './components/auth/login/login.component';
import {RegisterComponent} from './components/auth/register/register.component';
import {CashDepositComponent} from './components/cash-deposit/cash-deposit.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {NotFoundComponent} from './components/not-found/not-found.component';
import {UserCreateComponent} from './components/users/user-create/user-create.component';
import {UserDetailsComponent} from './components/users/user-details/user-details.component';
import {UsersComponent} from './components/users/users.component';
import {AuthGuard} from './guards/auth.guard';
import {UploadFileComponent} from './upload-file/upload-file.component';

const routes: Routes = [
    {
        path: '',
        component: DashboardComponent,
        canActivate: [AuthGuard],
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'accounts'
            },
            {
                path: 'accounts',
                component: AccountListComponent
            },
            {
                path: 'accounts/:id',
                component: AccountComponent
            },
            {
                path: 'accounts/:id/deposit-cash',
                component: CashDepositComponent
            },
            {
                path: 'users/all',
                component: UsersComponent
            },
            {
                path: 'users/create',
                component: UserCreateComponent
            },
            {
                path: 'users/:id',
                component: UserDetailsComponent
            },
            {
                path: 'users/:id/create-deposit-account',
                component: AccountDetailComponent
            },
            {
                path: 'users/:id/authentication-methods',
                component: AccountListComponent
            },
            {
                path: 'upload',
                component: UploadFileComponent
            },
        ]
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'register',
        component: RegisterComponent
    },
    {
        path: 'logout',
        redirectTo: 'login'
    },
    {
        path: '**',
        component: NotFoundComponent
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})

export class AppRoutingModule {
}
