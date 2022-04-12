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

import { NgModule, ModuleWithProviders } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import {
  ApiConfiguration,
  ApiConfigurationInterface,
} from './api-configuration';

import { PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService } from './services/psuaisprovides-access-to-online-banking-account-functionality.service';
import { OnlineBankingAccountInformationService } from './services/online-banking-account-information.service';
import { OnlineBankingConsentsService } from './services/online-banking-consents.service';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from './services/online-banking-authorization-provides-access-to-online-banking.service';
import { OnlineBankingPISCancellationService } from './services/online-banking-piscancellation.service';
import { OnlineBankingOauthAuthorizationService } from './services/online-banking-oauth-authorization.service';
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from './services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from './services/psupisprovides-access-to-online-banking-payment-functionality.service';
import { PSUSCAProvidesAccessToOneTimePasswordForStrongCustomerAuthenticationService } from './services/psuscaprovides-access-to-one-time-password-for-strong-customer-authentication.service';

/**
 * Provider for all Api services, plus ApiConfiguration
 */
@NgModule({
  imports: [HttpClientModule],
  exports: [HttpClientModule],
  declarations: [],
  providers: [
    ApiConfiguration,
    PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService,
    OnlineBankingAccountInformationService,
    OnlineBankingConsentsService,
    OnlineBankingAuthorizationProvidesAccessToOnlineBankingService,
    OnlineBankingPISCancellationService,
    OnlineBankingOauthAuthorizationService,
    PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService,
    PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService,
    PSUSCAProvidesAccessToOneTimePasswordForStrongCustomerAuthenticationService,
  ],
})
export class ApiModule {
  static forRoot(
    customParams: ApiConfigurationInterface
  ): ModuleWithProviders<ApiModule> {
    return {
      ngModule: ApiModule,
      providers: [
        {
          provide: ApiConfiguration,
          useValue: { rootUrl: customParams.rootUrl },
        },
      ],
    };
  }
}
