import {Component, Inject} from '@angular/core';

import {URL_PARAMS_PROVIDER} from './common/constants/constants';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {

    constructor(@Inject(URL_PARAMS_PROVIDER) params) {
    }

    public checkUrl(): number {
        const url = window.location.href;
        return url.indexOf('/login');
    }

    public checkUrlbank(): number {
        const url = window.location.href;
        return url.indexOf('/bank-offered');    }
}
