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
