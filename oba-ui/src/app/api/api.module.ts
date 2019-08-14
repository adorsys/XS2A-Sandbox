/* tslint:disable */
import { NgModule, ModuleWithProviders } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ApiConfiguration, ApiConfigurationInterface } from './api-configuration';

import { PSUAISService } from './services/psuais.service';
import { OnlineBankingAccountInformationService } from './services/online-banking-account-information.service';
import { OnlineBankingConsentsService } from './services/online-banking-consents.service';
import { OnlineBankingAuthorizationService } from './services/online-banking-authorization.service';
import { PSUPISCancellationService } from './services/psupiscancellation.service';
import { PSUPISService } from './services/psupis.service';
import { PSUSCAService } from './services/psusca.service';

/**
 * Provider for all Api services, plus ApiConfiguration
 */
@NgModule({
  imports: [
    HttpClientModule
  ],
  exports: [
    HttpClientModule
  ],
  declarations: [],
  providers: [
    ApiConfiguration,
    PSUAISService,
    OnlineBankingAccountInformationService,
    OnlineBankingConsentsService,
    OnlineBankingAuthorizationService,
    PSUPISCancellationService,
    PSUPISService,
    PSUSCAService
  ],
})
export class ApiModule {
  static forRoot(customParams: ApiConfigurationInterface): ModuleWithProviders {
    return {
      ngModule: ApiModule,
      providers: [
        {
          provide: ApiConfiguration,
          useValue: {rootUrl: customParams.rootUrl}
        }
      ]
    }
  }
}
