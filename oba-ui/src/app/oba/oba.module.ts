import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
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
import { ConsentsComponent } from './consents/consents.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { PeriodicPaymentsComponent } from './periodic-payments/periodic-payments.component';
import { ConvertBalancePipe } from '../pipes/convertBalance.pipe';

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
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    ClipboardModule,
    NgbDatepickerModule,
    NgbPaginationModule,
    NotFoundModule,
  ],
})
export class ObaModule {}
