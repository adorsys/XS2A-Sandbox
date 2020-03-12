import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {NavItem} from "../models/navItem.model";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(private router: Router) {
  }

  goToLogoLink(customLink: string, defaultLink: string) {
    if (customLink && customLink.includes('http' || 'https' || 'www')) {
      window.open(customLink);
    } else {
      this.router.navigateByUrl(defaultLink);
    }
  }

  navigateTo(navItem: NavItem) {
    const url = navItem.route;

    switch (navItem.type) {
      case "default":
        this.router.navigateByUrl('/' + url);
        break;
      case "markdown":
        this.router.navigateByUrl('/page/' + url);
        break;
      case "redirect":
        window.open(url);
        break;
      default:
        this.router.navigateByUrl('/');
    }
  }
}
