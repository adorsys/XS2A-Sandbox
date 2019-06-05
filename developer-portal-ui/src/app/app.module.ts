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
