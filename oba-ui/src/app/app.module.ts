import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, ErrorHandler, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgHttpLoaderModule } from 'ng-http-loader';

import { ApiModule } from './api/api.module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthGuard } from './common/guards/auth.guard';
import { InfoModule } from './common/info/info.module';
import { AuthInterceptor } from './common/interceptors/AuthInterceptor';
import { ObaErrorsHandler } from './common/interceptors/ObaErrorsHandler';
import { AisService } from './common/services/ais.service';
import { SettingsHttpService } from './common/services/settings-http.service';
import { ShareDataService } from './common/services/share-data.service';
import { InternalServerErrorComponent } from './internal-server-error/internal-server-error.component';
import { ObaModule } from './oba/oba.module';

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}

@NgModule({
  declarations: [
    AppComponent,
    InternalServerErrorComponent
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
    ObaModule
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
