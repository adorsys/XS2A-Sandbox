import {Component, Inject} from '@angular/core';
import {URL_PARAMS_PROVIDER} from "./common/constants/constants";
import {Router} from "@angular/router";
import {ObaUtils} from "./common/utils/oba-utils";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {

    constructor(
        @Inject(URL_PARAMS_PROVIDER) params,
        private router: Router
    ) {

        console.log('params: ', params);
        this.router.navigate(['./login'],
            ObaUtils.getQueryParams(params.encryptedConsentId, params.authorisationId));

    }

    public checkUrl = (): any => {
        const url = window.location.href;
        return url.indexOf('/login');
    }
}
