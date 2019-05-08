import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {BankOfferedComponent} from './bank-offered/bank-offered.component';
import {ScaSelectionComponent} from './sca-selection/sca-selection.component';
import {TanConfirmationComponent} from './tan-confirmation/tan-confirmation.component';
import {RoutingPath} from './common/models/routing-path.model';
import {LoginComponent} from './login/login.component';
import {ResultPageComponent} from './result-page/result-page.component';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: RoutingPath.LOGIN,
        component: LoginComponent,
    },
    {
        path: RoutingPath.RESULT,
        component: ResultPageComponent,
    },
    {
        path: RoutingPath.BANK_OFFERED,
        component: BankOfferedComponent,
    },
    {
        path: RoutingPath.TAN_CONFIRMATION,
        component: TanConfirmationComponent,
    },
    {
        path: RoutingPath.SELECT_SCA,
        component: ScaSelectionComponent,
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
