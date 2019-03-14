import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Config } from '../../../models/config';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private config;

  constructor(private httpClient: HttpClient) {
    this.config = this.getDefaultConfig();
  }

  loadConfig(): Promise<Config> {
    return this.httpClient.get<Config>(`/ui/config`).toPromise();
  }

  getConfig(): Config {
    return this.config;
  }

  setConfig(config: Config) {
    this.config = this.mergeResponseConfigIntoDefault(config, this.config);
  }

  private mergeResponseConfigIntoDefault(
    responseConfig: Object,
    defaultConfig: Object
  ): any {
    Object.keys(responseConfig).map(key => {
      const value = responseConfig[key];
      if (typeof value === 'object' && value !== null) {
        responseConfig[key] = this.mergeResponseConfigIntoDefault(
          value,
          defaultConfig[key]
        );
      } else if (typeof value === 'boolean') {
        responseConfig[key] = value;
      } else {
        responseConfig[key] = !value ? defaultConfig[key] : value;
      }
    });
    return responseConfig;
  }

  private getDefaultConfig() {
    const contentUrlsDe = {
      cert: 'assets/docs/de/create-cert-page.md',
      faq: 'assets/docs/de/faq-page.md',
      portal: 'assets/docs/de/developer-portal-page.md',
      testcases: 'assets/docs/de/test-cases.md',
    };

    const contentUrlsEn = {
      cert: 'assets/docs/en/create-cert-page.md',
      faq: 'assets/docs/en/faq-page.md',
      portal: 'assets/docs/en/developer-portal-page.md',
      testcases: 'assets/docs/en/test-cases.md',
    };

    return {
      contactMailto: 'mailto:psd2@adorsys.de',
      certPageEnabled: true,
      logoUrl: 'assets/logo-accelerator.svg',
      contentUrlsDe: contentUrlsDe,
      contentUrlsEn: contentUrlsEn,
    };
  }
}
