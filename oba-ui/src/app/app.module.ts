import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';

import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {ResultPageComponent} from './result-page/result-page.component';
import {TanSelectionComponent} from './ais/consent/tan-selection/tan-selection.component';
import {TanConfirmationComponent} from './ais/consent/tan-confirmation/tan-confirmation.component';
import {BankOfferedComponent} from './ais/consent/bank-offered/bank-offered.component';
import {AccountDetailsComponent} from './account-details/account-details.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AuthInterceptor} from "./common/interceptors/AuthInterceptor";
import {HttpClientModule} from "@angular/common/http";
import { ScaSelectionComponent } from './ais/consent/sca-selection/sca-selection.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        ResultPageComponent,
        TanSelectionComponent,
        TanConfirmationComponent,
        BankOfferedComponent,
        AccountDetailsComponent,
        ScaSelectionComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        ReactiveFormsModule,
        FormsModule,
        HttpClientModule
    ],
    providers: [
      {
        provide: HTTP_INTERCEPTORS,
        useClass: AuthInterceptor ,
        multi: true
      }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
