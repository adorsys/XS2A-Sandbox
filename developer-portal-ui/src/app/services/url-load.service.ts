import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UrlService } from './url.service';
import { EnvLink } from '../models/envLink.model';

@Injectable({
  providedIn: 'root',
})
export class UrlLoadService {
  private defaultContentFolderPath = '../assets/content';
  private urlJsonName = '/EnvLinks.json';
  private customContentFolderPath = '../assets/custom-content';
  constructor(private http: HttpClient, private urlService: UrlService) {}

  private static isJSONValid(url) {
    try {
      JSON.parse(JSON.stringify(url));
      return true;
    } catch (e) {
      console.log(e);
    }

    return false;
  }

  initializeUrls(): Promise<any> {
    return new Promise((resolve) => {
      this.getEnvUrl().then((url) => {
        this.http
          .get(url)
          .toPromise()
          .then((data: EnvLink) => {
            this.urlService.setUrl(data);
            resolve(true);
          });
      });
    });
  }
  private getEnvUrl(): Promise<string> {
    return this.http
      .get(this.customContentFolderPath + this.urlJsonName)
      .toPromise()
      .then(
        (url) => {
          if (UrlLoadService.isJSONValid(url)) {
            this.urlService.setCustom();
            return this.customContentFolderPath + this.urlJsonName;
          }
          return this.defaultContentFolderPath + this.urlJsonName;
        },
        () => {
          return this.defaultContentFolderPath + this.urlJsonName;
        }
      );
  }
}
