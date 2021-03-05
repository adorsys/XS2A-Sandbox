import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss'],
})
export class VerifyEmailComponent implements OnInit {
  constructor(private _router: Router) {}

  ngOnInit(): void {}
  closeWindow() {
    open(location.hostname, '_self').close();
  }
  /**
   * Check if the router url contains the specified route
   *
   * @param {string} route
   * @returns
   * @memberof VerifyEmailComponent
   */
  hasRoute(route: string) {
    return this._router.url.includes(route);
  }
}
