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

import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule, SecurityContext } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { GettingStartedComponent } from './components/getting-started/getting-started.component';
import { ContactComponent } from './components/contact/contact.component';
import { TestValuesComponent } from './components/test-cases/components/test-values/test-values.component';
import { AccinfAccountsGetComponent } from './components/test-cases/components/api-endpoints/accinf-accounts-get/accinf-accounts-get.component';
import { AccinfAccountGetComponent } from './components/test-cases/components/api-endpoints/accinf-account-get/accinf-account-get.component';
import { AccinfTransactionsGetComponent } from './components/test-cases/components/api-endpoints/accinf-transactions-get/accinf-transactions-get.component';
import { AccinfBalanceGetComponent } from './components/test-cases/components/api-endpoints/accinf-balance-get/accinf-balance-get.component';
import { AccinfTransactionGetComponent } from './components/test-cases/components/api-endpoints/accinf-transaction-get/accinf-transaction-get.component';
import { NavComponent } from './components/common/nav/nav.component';
import { FooterComponent } from './components/common/footer/footer.component';
import { TestCasesModule } from './components/test-cases/test-cases.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpLoaderFactory, LanguageService } from './services/language.service';
import { NgHttpLoaderModule } from 'ng-http-loader';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxImageZoomModule } from 'ngx-image-zoom';
import { MarkdownModule } from 'ngx-markdown';
import { DataService } from './services/data.service';
import { RestService } from './services/rest.service';
import { CustomPageComponent } from './components/custom-page/custom-page.component';
import { GoogleAnalyticsService } from './services/google-analytics.service';
import { CustomizeService } from './services/customize.service';
import { SettingsHttpService } from './services/settings-http.service';
import { UrlLoadService } from './services/url-load.service';
import { UrlService } from './services/url.service';
import { SanitizeHtmlPipe } from './pipes/sanitize-html.pipe';
import { MatSnackBarModule } from '@angular/material/snack-bar';

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}
export function url_Init(urlLoadService: UrlLoadService) {
  return () => urlLoadService.initializeUrls();
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
    SanitizeHtmlPipe,
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
    NgxImageZoomModule,
    MarkdownModule.forRoot({
      sanitize: SecurityContext.NONE,
    }),
    MatSnackBarModule,
  ],
  exports: [],
  providers: [
    RestService,
    DataService,
    LanguageService,
    CustomizeService,
    UrlService,
    GoogleAnalyticsService,
    {
      provide: APP_INITIALIZER,
      useFactory: app_Init,
      deps: [SettingsHttpService],
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: url_Init,
      deps: [UrlLoadService],
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
