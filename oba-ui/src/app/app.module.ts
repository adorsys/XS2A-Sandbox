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


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    ApiModule.forRoot({rootUrl: '/oba-proxy/'}),
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
