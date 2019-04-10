import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AisRoutingModule } from './ais-routing.module';
import { LoginComponent } from './login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { GrantConsentComponent } from './grant-consent/grant-consent.component';
import { AccountDetailsComponent } from './account-details/account-details.component';
import { SelectScaComponent } from './select-sca/select-sca.component';

@NgModule({
  declarations: [LoginComponent, GrantConsentComponent, AccountDetailsComponent, SelectScaComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AisRoutingModule
  ]
})
export class AisModule { }
