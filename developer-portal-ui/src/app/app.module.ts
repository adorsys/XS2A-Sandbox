import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from '../pages/home/home.component';
import { GettingStartedComponent } from '../pages/getting-started/getting-started.component';
import { FaqComponent } from '../pages/faq/faq.component';
import { TestCasesModule } from '../pages/test-cases/test-cases.module';
import { HttpClientModule } from '@angular/common/http';
import { ContactComponent } from '../pages/contact/contact.component';
import { RestService } from '../services/rest.service';
import { DataService } from '../services/data.service';
import { TestValuesComponent } from '../pages/test-cases/components/test-values/test-values.component';
import { AdminComponent } from '../pages/admin/admin.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SettingsModalComponent } from '../pages/settigs-modal/settings-modal.component';
import { AccinfAccountsGetComponent } from '../pages/test-cases/components/api-endpoints/accinf-accounts-get/accinf-accounts-get.component';
import { AccinfAccountGetComponent } from '../pages/test-cases/components/api-endpoints/accinf-account-get/accinf-account-get.component';
import { AccinfBalanceGetComponent } from '../pages/test-cases/components/api-endpoints/accinf-balance-get/accinf-balance-get.component';
import { AccinfTransactionsGetComponent } from '../pages/test-cases/components/api-endpoints/accinf-transactions-get/accinf-transactions-get.component';
import { AccinfTransactionGetComponent } from '../pages/test-cases/components/api-endpoints/accinf-transaction-get/accinf-transaction-get.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GettingStartedComponent,
    FaqComponent,
    ContactComponent,
    TestValuesComponent,
    AdminComponent,
    SettingsModalComponent,
    AccinfAccountsGetComponent,
    AccinfAccountGetComponent,
    AccinfBalanceGetComponent,
    AccinfTransactionsGetComponent,
    AccinfTransactionGetComponent,
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
    ReactiveFormsModule,
    FormsModule,
  ],
  exports: [SettingsModalComponent],
  providers: [RestService, DataService],
  bootstrap: [AppComponent],
})
export class AppModule {}
