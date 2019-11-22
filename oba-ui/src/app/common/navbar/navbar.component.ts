import { Component } from '@angular/core';

import { AuthService } from '../services/auth.service';
import { CustomizeService } from '../services/customize.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {

    constructor(public customizeService: CustomizeService,
                private authService: AuthService) {
    }

    onLogout(): void {
        this.authService.logout();
    }
}
