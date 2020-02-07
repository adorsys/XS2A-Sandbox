import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  AccountAccessManagementComponent,
} from './components/account-access-management/account-access-management.component';
import { AccountDetailComponent } from './components/account-detail/account-detail.component';
import { AccountListComponent } from './components/account-list/account-list.component';
import { AccountComponent } from './components/account/account.component';
import { ConfirmNewPasswordComponent } from './components/auth/confirm-new-password/confirm-new-password.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { ResetPasswordComponent } from './components/auth/reset-password/reset-password.component';
import { CashDepositComponent } from './components/cash-deposit/cash-deposit.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { TestDataGenerationComponent } from './components/testDataGeneration/test-data-generation.component';
import { UserProfileUpdateComponent } from './components/user-profile-update/user-profile-update.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { UserDetailsComponent } from './components/users/user-details/user-details.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { UsersComponent } from './components/users/users.component';
import { AuthGuard } from './guards/auth.guard';
import { UploadFileComponent } from './uploadFile/uploadFile.component';

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
                path: 'accounts/:id/access',
                component: AccountAccessManagementComponent
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
                path: 'users/:id/update-user-details',
                component: UserUpdateComponent
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
            {
                path: 'generate-test-data',
                component: TestDataGenerationComponent
            },
            {
                path: 'profile',
                component: UserProfileComponent
            },
            {
                path: 'profile/edit',
                component: UserProfileUpdateComponent
            }
        ]
    },
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
        component: ConfirmNewPasswordComponent
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
