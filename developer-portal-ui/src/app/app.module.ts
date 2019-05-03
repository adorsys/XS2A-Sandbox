import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from '../pages/home/home.component';
import { GettingStartedComponent } from '../pages/getting-started/getting-started.component';
import { FaqComponent } from '../pages/faq/faq.component';
import { TestCasesModule } from '../pages/test-cases/test-cases.module';
import { HttpClientModule } from '@angular/common/http';
import { ContactComponent } from '../pages/contact/contact.component';
import { RestService } from '../services/rest.service';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GettingStartedComponent,
    FaqComponent,
    ContactComponent,
  ],
  imports: [BrowserModule, TestCasesModule, AppRoutingModule, HttpClientModule],
  providers: [RestService],
  bootstrap: [AppComponent],
})
export class AppModule {}
