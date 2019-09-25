/* tslint:disable */
import { Injectable } from '@angular/core';

/**
 * Global configuration for Api services
 */
@Injectable({
  providedIn: 'root',
})
export class ApiConfiguration {
  rootUrl: string = '//dev-dynamicsandbox-onlinebanking.cloud.adorsys.de';
}

export interface ApiConfigurationInterface {
  rootUrl?: string;
}
