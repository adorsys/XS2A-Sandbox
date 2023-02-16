/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import { EnvLink } from '../models/envLink.model';
import { of, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UrlService {
  private _custom = false;
  private _defaultContentFolderPath = '../assets/content';
  private _customContentFolderPath = '../assets/custom-content';

  private currentUrl: Observable<any>;

  constructor() {}

  setUrl(envLink: EnvLink) {
    if (envLink) {
      this.currentUrl = of(this.setUpRoutes(envLink));
    } else {
      this.currentUrl = of({});
    }
  }
  get custom(): boolean {
    return this._custom;
  }

  get defaultContentFolder(): string {
    return this._defaultContentFolderPath;
  }

  get customContentFolder(): string {
    return this._customContentFolderPath;
  }

  getUrl() {
    return this.currentUrl;
  }

  private setUpRoutes(envLink: EnvLink) {
    if (envLink.servicesAvailable.ledgers && envLink.servicesAvailable.ledgers.environmentLink) {
      envLink.servicesAvailable.ledgers.environmentLink = `${envLink.servicesAvailable.ledgers.environmentLink}`;
    }
    return envLink;
  }
  setCustom() {
    this._custom = true;
  }
}
