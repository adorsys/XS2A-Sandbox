import { Component, DoCheck } from '@angular/core';

import { AuthService } from '../../services/auth.service';
import { CustomizeService } from '../../services/customize.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements DoCheck {

    constructor(private authService: AuthService,
                public customizeService: CustomizeService) {}

    ngDoCheck(): void {
        if (!this.authService.isLoggedIn()) {
            this.authService.logout();
            throw new Error('Session expired. Please login again.');
        }
    }

    onLogout(): void {
        this.authService.logout();
    }
}
