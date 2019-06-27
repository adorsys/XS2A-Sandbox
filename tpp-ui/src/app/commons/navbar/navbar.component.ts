import {Component, DoCheck, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, DoCheck {

    constructor(private authService: AuthService) {
    }

    ngOnInit() {
    }

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
