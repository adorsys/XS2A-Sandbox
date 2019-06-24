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

const routes: Routes = [
  {
    path: 'test-cases',
    component: TestCasesComponent,
    children: [
      {
        path: 'testing-flows',
        component: TestingFlowsComponent,
      },
      {
        path: 'test-values',
        component: TestValuesComponent,
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
        path: 'postman-testing',
        component: PostmanTestingComponent,
      },
      {
        path: 'redirect-consent-post',
        component: RdctConsentPOSTComponent,
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
        path: 'embedded-consent-create-post',
        component: EmbConsentCreatePostComponent,
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
        path: 'embedded-payment-cancellation-post',
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
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TestCasesRoutingModule {}
