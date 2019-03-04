import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { TanSelectionComponent } from './ais/consent/tan-selection/tan-selection.component';
import { TanConfirmationComponent } from './ais/consent/tan-confirmation/tan-confirmation.component';
import { BankOfferedComponent } from './ais/consent/bank-offered/bank-offered.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ResultPageComponent,
    TanSelectionComponent,
    TanConfirmationComponent,
    BankOfferedComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
