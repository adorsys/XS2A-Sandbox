import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ErrorHandler, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AuthInterceptor} from './common/interceptors/AuthInterceptor';
import {AisService} from './common/services/ais.service';
import {ShareDataService} from './common/services/share-data.service';
import {ObaErrorsHandler} from "./common/interceptors/ObaErrorsHandler";
import {NgHttpLoaderModule} from "ng-http-loader";
import {ApiModule} from "./api/api.module";
import {InternalServerErrorComponent} from './internal-server-error/internal-server-error.component';
import {InfoModule} from "./common/info/info.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";


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
    NgHttpLoaderModule.forRoot()
  ],
  providers: [
    AisService,
    ShareDataService,
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
