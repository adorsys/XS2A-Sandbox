/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  NgbDatepickerModule,
  NgbModule,
  NgbPaginationModule,
} from '@ng-bootstrap/ng-bootstrap';
import { ClipboardModule } from 'ngx-clipboard';

import { NavbarComponent } from '../common/navbar/navbar.component';
import { SidebarComponent } from '../common/sidebar/sidebar.component';
import { NotFoundModule } from '../not-found/not-found.module';
import { AccountDetailsComponent } from './accounts/account-details/account-details.component';
import { AccountsComponent } from './accounts/accounts.component';
import { ConfirmPasswordComponent } from './confirm-password/confirm-password.component';
import { ConsentsComponent } from './consents/consents.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { PeriodicPaymentsComponent } from './periodic-payments/periodic-payments.component';
import { ConvertBalancePipe } from '../pipes/convertBalance.pipe';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { UserProfileUpdateComponent } from './user-profile-update/user-profile-update.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

@NgModule({
  declarations: [
    LoginComponent,
    DashboardComponent,
    NavbarComponent,
    SidebarComponent,
    ConsentsComponent,
    AccountsComponent,
    AccountDetailsComponent,
    ResetPasswordComponent,
    ConfirmPasswordComponent,
    PeriodicPaymentsComponent,
    ConvertBalancePipe,
    UserProfileComponent,
    UserProfileUpdateComponent,
    VerifyEmailComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    ClipboardModule,
    NgbDatepickerModule,
    NgbPaginationModule,
    NotFoundModule,
  ],
})
export class ObaModule {}
