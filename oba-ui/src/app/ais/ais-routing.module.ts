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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoutingPath } from '../common/models/routing-path.model';
import { LoginComponent } from './login/login.component';
import { GrantConsentComponent } from './grant-consent/grant-consent.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { TanConfirmationComponent } from './tan-confirmation/tan-confirmation.component';
import { NotFoundComponent } from '../not-found/not-found.component';

const routes: Routes = [
  {
    path: RoutingPath.LOGIN,
    component: LoginComponent,
  },
  {
    path: RoutingPath.GRANT_CONSENT,
    component: GrantConsentComponent,
  },
  {
    path: RoutingPath.SELECT_SCA,
    component: SelectScaComponent,
  },
  {
    path: RoutingPath.TAN_CONFIRMATION,
    component: TanConfirmationComponent,
  },
  {
    path: RoutingPath.RESULT,
    component: ResultPageComponent,
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AisRoutingModule {}
