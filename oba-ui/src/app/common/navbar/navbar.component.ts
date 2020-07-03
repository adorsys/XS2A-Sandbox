import { Component, DoCheck, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CustomizeService } from '../services/customize.service';
import { ShareDataService } from '../services/share-data.service';
import { CurrentUserService } from '../services/current-user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit, DoCheck {
  public title: string;
  public obaUser;
  public toggleFlag = false;

  constructor(
    public customizeService: CustomizeService,
    private authService: AuthService,
    private shareDataService: ShareDataService,
    private currentUserService: CurrentUserService
  ) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.currentUserService
        .getCurrentUser()
        .subscribe((data) => (this.obaUser = data.body));
      this.shareDataService.currentUser.subscribe(
        (data) => (this.obaUser = data)
      );
    }
  }

  ngDoCheck(): void {
    if (!this.authService.isLoggedIn()) {
      this.authService.logout();
      throw new Error('Session expired. Please login again.');
    }
    this.title = this.customizeService.getTitle();
  }

  onLogout(): void {
    this.authService.logout();
  }

  showDropdown() {
    this.toggleFlag = !this.toggleFlag;
  }
}
