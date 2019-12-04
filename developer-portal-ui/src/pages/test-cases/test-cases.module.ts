import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RedirectComponent } from './components/redirect/redirect.component';
import { TestCasesComponent } from './test-cases.component';
import { TestCasesRoutingModule } from './test-cases-routing.module';
import { EmbeddedComponent } from './components/embedded/embedded.component';
import { TestingFlowsComponent } from './components/testing-flows/testing-flows.component';
import { PostmanTestingComponent } from './components/postman-testing/postman-testing.component';
import { RdctConsentPOSTComponent } from './components/api-endpoints/rdct-consent-post/rdct-consent-post.component';
import { RdctPaymentCancellationPostComponent } from './components/api-endpoints/rdct-payment-cancellation-post/rdct-payment-cancellation-post.component';
import { LineCommandComponent } from '../../custom-elements/line-command/line-command.component';
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
import { PlayWthDataComponent } from './components/play-with-data/play-wth-data.component';
import { FormsModule } from '@angular/forms';
import { ngxLoadingAnimationTypes, NgxLoadingModule } from 'ngx-loading';
import { CodeAreaComponent } from '../../custom-elements/code-area/code-area.component';
import { PrettyJsonPipe } from '../../pipes/pretty-json.pipe';
import { TranslateModule } from '@ngx-translate/core';
import { NgxImageZoomModule } from 'ngx-image-zoom';

@NgModule({
  declarations: [
    RedirectComponent,
    TestCasesComponent,
    EmbeddedComponent,
    TestingFlowsComponent,
    PostmanTestingComponent,
    LineCommandComponent,
    CodeAreaComponent,
    RdctConsentPOSTComponent,
    RdctPaymentCancellationPostComponent,
    RdctPaymentCancellationDeleteComponent,
    RdctPaymentInitiationPostComponent,
    EmbConsentCreatePostComponent,
    EmbConsentAuthPostComponent,
    EmbConsentPutComponent,
    EmbConsentGetComponent,
    EmbPaymentCancellPostComponent,
    EmbPaymentCancellDeleteComponent,
    EmbPaymentCancellationPostComponent,
    EmbPaymentCancellPutComponent,
    EmbPaymentCancellGetComponent,
    EmbPaymentInitCreatePostComponent,
    EmbPaymentInitAuthPostComponent,
    EmbPaymentInitPutComponent,
    EmbPaymentInitGetComponent,
    PlayWthDataComponent,
    PrettyJsonPipe,
  ],
  imports: [
    CommonModule,
    TestCasesRoutingModule,
    FormsModule,
    NgxLoadingModule.forRoot({
      animationType: ngxLoadingAnimationTypes.wanderingCubes,
    }),
    TranslateModule,
    NgxImageZoomModule,
  ],
  exports: [LineCommandComponent, PlayWthDataComponent, CodeAreaComponent],
})
export class TestCasesModule {}
