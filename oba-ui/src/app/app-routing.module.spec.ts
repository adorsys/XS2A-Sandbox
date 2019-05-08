import {BankOfferedComponent} from './bank-offered/bank-offered.component';
import {ScaSelectionComponent} from './sca-selection/sca-selection.component';
import {TanConfirmationComponent} from './tan-confirmation/tan-confirmation.component';
import {routes} from './app-routing.module';
import {RoutingPath} from './common/models/routing-path.model';
import {LoginComponent} from './login/login.component';
import {ResultPageComponent} from './result-page/result-page.component';



describe('Routes', () => {

    it('Should contain the route for / login', () => {
        expect(routes).toContain({ path: '', redirectTo: '/login', pathMatch: 'full' });
    });

    it('Should contain the route for /result', () => {
        expect(routes).toContain({path: RoutingPath.RESULT, component: ResultPageComponent});
    });

    it('Should contain the route for /bank-offered', () => {
        expect(routes).toContain({path: RoutingPath.BANK_OFFERED, component: BankOfferedComponent});
    });

    it('Should contain the route for /tan-confirm', () => {
        expect(routes).toContain({path: RoutingPath.TAN_CONFIRMATION, component: TanConfirmationComponent});
    });

    it('Should contain the route for /select-sca', () => {
        expect(routes).toContain({path: RoutingPath.SELECT_SCA, component: ScaSelectionComponent});
    });

});
