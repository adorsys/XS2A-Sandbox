import {routes} from './app-routing.module';
import {RoutingPath} from './common/models/routing-path.model';
import {LoginComponent} from './login/login.component';
import {ResultPageComponent} from './result-page/result-page.component';



describe('Routes', () => {

    it('Should contain the route for / login', () => {
        expect(routes).toContain({path: RoutingPath.LOGIN , component: LoginComponent});
    });

    it('Should contain the route for /tan-procedure', () => {
        expect(routes).toContain({path: RoutingPath.RESULT, component: ResultPageComponent});
    });
});
