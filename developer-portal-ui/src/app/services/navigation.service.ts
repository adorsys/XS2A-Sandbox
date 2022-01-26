/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { NavItem } from '../models/navItem.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  constructor(private router: Router, private http: HttpClient) {}

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
      case 'default':
        this.router.navigateByUrl('/' + url);
        break;
      case 'markdown':
        this.router.navigateByUrl('/page/' + url);
        break;
      case 'redirect':
        window.open(url);
        break;
      default:
        this.router.navigateByUrl('/');
    }
  }

  getNavigation(path: string): Promise<Array<NavItem>> {
    return this.http
      .get(`${path}/navigation.json`)
      .toPromise()
      .then((data) => {
        return data['navigation'];
      });
  }
}
