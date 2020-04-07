import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from "./components/home/home.component";
import {GettingStartedComponent} from "./components/getting-started/getting-started.component";
import {ContactComponent} from "./components/contact/contact.component";
import {TestValuesComponent} from "./components/test-cases/components/test-values/test-values.component";
import {AccinfAccountsGetComponent} from "./components/test-cases/components/api-endpoints/accinf-accounts-get/accinf-accounts-get.component";
import {AccinfAccountGetComponent} from "./components/test-cases/components/api-endpoints/accinf-account-get/accinf-account-get.component";
import {AccinfTransactionsGetComponent} from "./components/test-cases/components/api-endpoints/accinf-transactions-get/accinf-transactions-get.component";
import {AccinfBalanceGetComponent} from "./components/test-cases/components/api-endpoints/accinf-balance-get/accinf-balance-get.component";
import {AccinfTransactionGetComponent} from "./components/test-cases/components/api-endpoints/accinf-transaction-get/accinf-transaction-get.component";
import {NavComponent} from "./components/common/nav/nav.component";
import {FooterComponent} from "./components/common/footer/footer.component";
import {TestCasesModule} from "./components/test-cases/test-cases.module";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpLoaderFactory, LanguageService} from "./services/language.service";
import {NgHttpLoaderModule} from "ng-http-loader";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgxImageZoomModule} from "ngx-image-zoom";
import {MarkdownModule} from "ngx-markdown";
import {DataService} from "./services/data.service";
import {RestService} from "./services/rest.service";
import {CustomPageComponent} from './components/custom-page/custom-page.component';
import {GoogleAnalyticsService} from "./services/google-analytics.service";
import {CustomizeService} from "./services/customize.service";
import {SettingsHttpService} from "./services/settings-http.service";

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GettingStartedComponent,
    ContactComponent,
    TestValuesComponent,
    AccinfAccountsGetComponent,
    AccinfAccountGetComponent,
    AccinfBalanceGetComponent,
    AccinfTransactionsGetComponent,
    AccinfTransactionGetComponent,
    NavComponent,
    FooterComponent,
    CustomPageComponent,
  ],
  imports: [
    BrowserModule,
    TestCasesModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 1300,
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    NgHttpLoaderModule.forRoot(),
    ReactiveFormsModule,
    FormsModule,
    NgxImageZoomModule.forRoot(),
    MarkdownModule.forRoot(),
  ],
  exports: [],
  providers: [
    RestService,
    DataService,
    LanguageService,
    CustomizeService,
    GoogleAnalyticsService,
    {provide: APP_INITIALIZER, useFactory: app_Init, deps: [SettingsHttpService], multi: true}
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
