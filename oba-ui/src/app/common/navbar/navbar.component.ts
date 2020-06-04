import { Component, DoCheck, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CustomizeService } from '../services/customize.service';
import { UserTO } from '../../api/models';
import { OnlineBankingService } from '../services/online-banking.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit, DoCheck {
  public title: string;
  public obaUser: UserTO;
  public toggleFlag = false;
  constructor(
    public customizeService: CustomizeService,
    private authService: AuthService,
    private onlineBankingService: OnlineBankingService
  ) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.onlineBankingService
        .getCurrentUser()
        .subscribe((data) => (this.obaUser = data.body));
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
