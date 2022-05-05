import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  NgbDatepickerModule,
  NgbPaginationModule,
} from '@ng-bootstrap/ng-bootstrap';
import { ClipboardModule } from 'ngx-clipboard';

import { NavbarComponent } from '../common/navbar/navbar.component';
import { SidebarComponent } from '../common/sidebar/sidebar.component';
import { NotFoundModule } from '../not-found/not-found.module';
import { AccountDetailsComponent } from './accounts/account-details/account-details.component';
import { AccountsComponent } from './accounts/accounts.component';
import { ConfirmPasswordComponent } from './confirm-password/confirm-password.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { PeriodicPaymentsComponent } from './periodic-payments/periodic-payments.component';
import { ConvertBalancePipe } from '../pipes/convertBalance.pipe';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { UserProfileUpdateComponent } from './user-profile-update/user-profile-update.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';
import { MatTreeModule } from '@angular/material/tree';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ConsentPiisComponent } from './consents/piis/consent-piis.component';
import { ConsentAisComponent } from './consents/ais/consent-ais.component';

@NgModule({
  declarations: [
    LoginComponent,
    DashboardComponent,
    NavbarComponent,
    SidebarComponent,
    AccountsComponent,
    AccountDetailsComponent,
    ResetPasswordComponent,
    ConfirmPasswordComponent,
    PeriodicPaymentsComponent,
    ConvertBalancePipe,
    UserProfileComponent,
    UserProfileUpdateComponent,
    VerifyEmailComponent,
    ConsentPiisComponent,
    ConsentAisComponent,
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
    MatTreeModule,
    MatIconModule,
    MatButtonModule,
  ],
})
export class ObaModule {}
