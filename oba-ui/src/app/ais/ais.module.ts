import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AisRoutingModule } from './ais-routing.module';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GrantConsentComponent } from './grant-consent/grant-consent.component';
import { AccountDetailsComponent } from './account-details/account-details.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { TanConfirmationComponent } from './tan-confirmation/tan-confirmation.component';
import { NotFoundModule } from '../not-found/not-found.module';

@NgModule({
  declarations: [
    LoginComponent,
    GrantConsentComponent,
    AccountDetailsComponent,
    SelectScaComponent,
    ResultPageComponent,
    TanConfirmationComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AisRoutingModule,
    NotFoundModule,
  ],
})
export class AisModule {}
