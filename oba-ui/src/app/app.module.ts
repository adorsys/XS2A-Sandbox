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
import { MatSnackBarModule } from '@angular/material/snack-bar';

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}

@NgModule({
  declarations: [AppComponent, InternalServerErrorComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    InfoModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    ApiModule.forRoot({ rootUrl: '/oba-proxy' }),
    NgHttpLoaderModule.forRoot(),
    ObaModule,
    MatSnackBarModule,
  ],
  providers: [
    AisService,
    ShareDataService,
    AuthGuard,
    {
      provide: APP_INITIALIZER,
      useFactory: app_Init,
      deps: [SettingsHttpService],
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
    {
      provide: ErrorHandler,
      useClass: ObaErrorsHandler,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
