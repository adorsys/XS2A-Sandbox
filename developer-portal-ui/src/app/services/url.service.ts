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
