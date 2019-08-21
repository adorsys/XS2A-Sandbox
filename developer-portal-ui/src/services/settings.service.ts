import { Injectable, isDevMode } from '@angular/core';
import { Settings } from '../models/settings.model';
import { EnvLink } from '../models/envLinks.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SettingsService {
  public settings: Settings;
  private defaultEnvLinks: EnvLink[] = [
    {
      name: 'DEVELOPER_PORTAL',
      localEnvironmentLink: 'http://localhost:4206',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/',
    },
    {
      name: 'XS2A_INTERFACE_SWAGGER',
      localEnvironmentLink: 'http://localhost:8089/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/',
    },
    {
      name: 'CONSENT_MANAGEMENT_SYSTEM',
      localEnvironmentLink: 'http://localhost:38080/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-cms.cloud.adorsys.de',
    },
    {
      name: 'LEDGERS',
      localEnvironmentLink: 'http://localhost:8088/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-ledgers.cloud.adorsys.de',
    },
    {
      name: 'ASPSP_PROFILE_SWAGGER',
      localEnvironmentLink: 'http://localhost:48080/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-aspspprofile.cloud.adorsys.de',
    },
    {
      name: 'TPP_UI',
      localEnvironmentLink: 'http://localhost:4205',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-tppui.cloud.adorsys.de/login',
    },
    {
      name: 'ONLINE_BANKING_UI',
      localEnvironmentLink: 'http://localhost:4400',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-onlinebankingui.cloud.adorsys.de/account-information/login',
    },
    {
      name: 'ONLINE_BANKING_BACKEND',
      localEnvironmentLink: 'http://localhost:8090/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-onlinebanking.cloud.adorsys.de/swagger-ui.html',
    },
    {
      name: 'CERTIFICATE_GENERATOR',
      localEnvironmentLink: 'http://localhost:8092/swagger-ui.html',
      productionEnvironmentLink:
        'https://demo-dynamicsandbox-certificategenerator.cloud.adorsys.de/swagger-ui.html',
    },
  ];

  constructor() {
    this.settings = new Settings();
  }

  public getEnvLink(name: string): string {
    let link = '';
    this.settings.envLinks.forEach(envLink => {
      if (name === envLink.name) {
        link = environment.production
          ? envLink.productionEnvironmentLink
          : envLink.localEnvironmentLink;
      }
    });
    return link;
  }

  public fallbackToDefault(): void {
    this.settings.envLinks = this.defaultEnvLinks;
  }
}
