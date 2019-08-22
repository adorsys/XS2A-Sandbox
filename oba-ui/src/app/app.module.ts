import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {APP_INITIALIZER, ErrorHandler, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AuthInterceptor} from './common/interceptors/AuthInterceptor';
import {AisService} from './common/services/ais.service';
import {ShareDataService} from './common/services/share-data.service';
import {ObaErrorsHandler} from './common/interceptors/ObaErrorsHandler';
import {NgHttpLoaderModule} from 'ng-http-loader';
import {ApiModule} from './api/api.module';
import {InternalServerErrorComponent} from './internal-server-error/internal-server-error.component';
import {InfoModule} from './common/info/info.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {SettingsHttpService} from './common/services/settings-http.service';
import {LoginComponent} from './login/login.component';
import {ConsentsComponent} from './consents/consents.component';
import {AuthGuard} from './common/guards/auth.guard';
import {DashboardComponent} from './dashboard/dashboard.component';
import {NavbarComponent} from './common/navbar/navbar.component';
import {SidebarComponent} from './common/sidebar/sidebar.component';
import { AccountsComponent } from './accounts/accounts.component';
import { AccountDetailsComponent } from './accounts/account-details/account-details.component';
import { ClipboardModule } from 'ngx-clipboard';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { ConfirmPasswordComponent } from './confirm-password/confirm-password.component';

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    NavbarComponent,
    SidebarComponent,
    ConsentsComponent,
    InternalServerErrorComponent,
    AccountsComponent,
    AccountDetailsComponent,
    ResetPasswordComponent,
    ConfirmPasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    InfoModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    ApiModule.forRoot({rootUrl: '/oba-proxy'}),
    NgHttpLoaderModule.forRoot(),
    ClipboardModule,
    NgbDatepickerModule
  ],
  providers: [
    AisService,
    ShareDataService,
    AuthGuard,
    { provide: APP_INITIALIZER, useFactory: app_Init, deps: [SettingsHttpService], multi: true },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: ErrorHandler,
      useClass: ObaErrorsHandler
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
