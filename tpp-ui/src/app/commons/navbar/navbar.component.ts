import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Subscription} from "rxjs";
import {AutoLogoutService} from "../../services/auto-logout.service";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

    subscription = new Subscription();

    constructor(private authService: AuthService, private autoLogoutService: AutoLogoutService) {
    }

    ngOnInit() {
        this.startTokenMonitoring();
    }

    // subscribe every minute and check if token is still valid
    startTokenMonitoring(): void {
        this.subscription = this.autoLogoutService.timerSubject.subscribe(time => {
            if (!this.authService.isLoggedIn()) {
              this.onLogout();
            }
        })
    }

    onLogout(): void {
        this.authService.logout();
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}
