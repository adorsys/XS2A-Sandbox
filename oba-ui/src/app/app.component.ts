import {Component} from '@angular/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {

    constructor() {
    }

    public checkUrl(): number {
        const url = window.location.href;
        return url.indexOf('/login');
    }

    public checkUrlbank(): number {
        const url = window.location.href;
        return url.indexOf('/bank-offered');    }
}
