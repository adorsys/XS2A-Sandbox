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

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TestCasesComponent } from './test-cases.component';
import { TestingFlowsComponent } from './components/testing-flows/testing-flows.component';
import { EmbeddedComponent } from './components/embedded/embedded.component';
import { RedirectComponent } from './components/redirect/redirect.component';
import { PostmanTestingComponent } from './components/postman-testing/postman-testing.component';
import { RdctConsentPOSTComponent } from './components/api-endpoints/rdct-consent-post/rdct-consent-post.component';
import { RdctPaymentCancellationPostComponent } from './components/api-endpoints/rdct-payment-cancellation-post/rdct-payment-cancellation-post.component';
import { RdctPaymentCancellationDeleteComponent } from './components/api-endpoints/rdct-payment-cancellation-delete/rdct-payment-cancellation-delete.component';
import { RdctPaymentInitiationPostComponent } from './components/api-endpoints/rdct-payment-initiation-post/rdct-payment-initiation-post.component';
import { EmbConsentCreatePostComponent } from './components/api-endpoints/emb-consent-create-post/emb-consent-create-post.component';
import { EmbConsentAuthPostComponent } from './components/api-endpoints/emb-consent-auth-post/emb-consent-auth-post.component';
import { EmbConsentPutComponent } from './components/api-endpoints/emb-consent-put/emb-consent-put.component';
import { EmbConsentGetComponent } from './components/api-endpoints/emb-consent-get/emb-consent-get.component';
import { EmbPaymentCancellPostComponent } from './components/api-endpoints/emb-payment-cancell-post/emb-payment-cancell-post.component';
import { EmbPaymentCancellDeleteComponent } from './components/api-endpoints/emb-payment-cancell-delete/emb-payment-cancell-delete.component';
import { EmbPaymentCancellationPostComponent } from './components/api-endpoints/emb-payment-cancellation-post/emb-payment-cancellation-post.component';
import { EmbPaymentCancellPutComponent } from './components/api-endpoints/emb-payment-cancell-put/emb-payment-cancell-put.component';
import { EmbPaymentCancellGetComponent } from './components/api-endpoints/emb-payment-cancell-get/emb-payment-cancell-get.component';
import { EmbPaymentInitCreatePostComponent } from './components/api-endpoints/emb-payment-init-create-post/emb-payment-init-create-post.component';
import { EmbPaymentInitAuthPostComponent } from './components/api-endpoints/emb-payment-init-auth-post/emb-payment-init-auth-post.component';
import { EmbPaymentInitPutComponent } from './components/api-endpoints/emb-payment-init-put/emb-payment-init-put.component';
import { EmbPaymentInitGetComponent } from './components/api-endpoints/emb-payment-init-get/emb-payment-init-get.component';
import { TestValuesComponent } from './components/test-values/test-values.component';
import { AccinfAccountsGetComponent } from './components/api-endpoints/accinf-accounts-get/accinf-accounts-get.component';
import { AccinfAccountGetComponent } from './components/api-endpoints/accinf-account-get/accinf-account-get.component';
import { AccinfBalanceGetComponent } from './components/api-endpoints/accinf-balance-get/accinf-balance-get.component';
import { AccinfTransactionsGetComponent } from './components/api-endpoints/accinf-transactions-get/accinf-transactions-get.component';
import { AccinfTransactionGetComponent } from './components/api-endpoints/accinf-transaction-get/accinf-transaction-get.component';
import { RdctPaymentStatusGetComponent } from './components/api-endpoints/rdct-payment-status-get/rdct-payment-status-get.component';
import { FundsConfirmationComponent } from './components/api-endpoints/funds-confirmation/funds-confirmation.component';
import { DescriptionTestcaseComponent } from './components/description-testcase/description-testcase.component';
import { RdctPaymentInitGetComponent } from './components/api-endpoints/rdct-payment-init-get/rdct-payment-init-get.component';
import { EmbPaymentStatusGetComponent } from './components/api-endpoints/emb-payment-status-get/emb-payment-status-get.component';
import { EmbPaymentInitPutTanComponent } from './components/api-endpoints/emb-payment-init-put-tan/emb-payment-init-put-tan.component';
import { EmbConsentPutTanComponent } from './components/api-endpoints/emb-consent-put-tan/emb-consent-put-component-tan.component';

const routes: Routes = [
  {
    path: 'test-cases',
    component: TestCasesComponent,
    children: [
      {
        path: 'description',
        component: TestValuesComponent,
      },
      {
        path: 'test-values',
        component: TestValuesComponent,
      },
      {
        path: 'testing-flows',
        component: TestingFlowsComponent,
      },
      {
        path: 'embedded',
        component: EmbeddedComponent,
      },
      {
        path: 'redirect',
        component: RedirectComponent,
      },
      {
        path: 'description-testcase',
        component: DescriptionTestcaseComponent,
      },
      {
        path: 'postman-testing',
        component: PostmanTestingComponent,
      },
      {
        path: 'redirect-consent-post',
        component: RdctConsentPOSTComponent,
      },
      {
        path: 'redirect-payment-init-get',
        component: RdctPaymentInitGetComponent,
      },
      {
        path: 'redirect-cancellation-post',
        component: RdctPaymentCancellationPostComponent,
      },
      {
        path: 'redirect-cancellation-delete',
        component: RdctPaymentCancellationDeleteComponent,
      },
      {
        path: 'redirect-payment-initiation-post',
        component: RdctPaymentInitiationPostComponent,
      },
      {
        path: 'redirect-payment-status-get',
        component: RdctPaymentStatusGetComponent,
      },
      {
        path: 'embedded-consent-create-post',
        component: EmbConsentCreatePostComponent,
      },
      {
        path: 'embedded-consent-put-tan',
        component: EmbConsentPutTanComponent,
      },
      {
        path: 'emb_get_payment_status',
        component: EmbPaymentStatusGetComponent,
      },
      {
        path: 'embedded-consent-auth-post',
        component: EmbConsentAuthPostComponent,
      },
      {
        path: 'embedded-consent-put',
        component: EmbConsentPutComponent,
      },
      {
        path: 'embedded-consent-get',
        component: EmbConsentGetComponent,
      },
      {
        path: 'embedded-payment-cancellation-post-1',
        component: EmbPaymentCancellPostComponent,
      },
      {
        path: 'embedded-payment-cancellation-delete',
        component: EmbPaymentCancellDeleteComponent,
      },
      {
        path: 'embedded-payment-cancellation-post-2',
        component: EmbPaymentCancellationPostComponent,
      },
      {
        path: 'embedded-payment-cancellation-put',
        component: EmbPaymentCancellPutComponent,
      },
      {
        path: 'embedded-payment-cancellation-put-tan',
        component: EmbPaymentInitPutTanComponent,
      },
      {
        path: 'embedded-payment-cancellation-get',
        component: EmbPaymentCancellGetComponent,
      },
      {
        path: 'embedded-payment-initiation-create-post',
        component: EmbPaymentInitCreatePostComponent,
      },
      {
        path: 'embedded-payment-initiation-auth-post',
        component: EmbPaymentInitAuthPostComponent,
      },
      {
        path: 'embedded-payment-initiation-put',
        component: EmbPaymentInitPutComponent,
      },
      {
        path: 'embedded-payment-initiation-get',
        component: EmbPaymentInitGetComponent,
      },
      {
        path: 'account-information-accounts-get',
        component: AccinfAccountsGetComponent,
      },
      {
        path: 'account-information-account-get',
        component: AccinfAccountGetComponent,
      },
      {
        path: 'account-information-balance-get',
        component: AccinfBalanceGetComponent,
      },
      {
        path: 'account-information-transactions-get',
        component: AccinfTransactionsGetComponent,
      },
      {
        path: 'account-information-transaction-get',
        component: AccinfTransactionGetComponent,
      },
      {
        path: 'funds-confirmation',
        component: FundsConfirmationComponent,
      },
      {
        path: 'emb-payment-init-get',
        component: EmbPaymentInitGetComponent,
      },
      {
        path: 'emb-payment-init-put-tan',
        component: EmbPaymentInitPutTanComponent,
      },
      {
        path: '**',
        redirectTo: 'testing-flows',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TestCasesRoutingModule {}
