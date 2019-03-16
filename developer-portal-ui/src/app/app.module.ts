import { BrowserModule } from '@angular/platform-browser';
import { MarkdownModule, MarkedOptions, MarkedRenderer } from 'ngx-markdown';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MockMarkdownComponent } from './common/mock-markdown.component';
import { ConfigService } from './common/services/config.service';
import { initializer } from './app-init';
import { FeatureEnabledGuard } from './common/FeatureEnabledGuard';

@NgModule({
  declarations: [AppComponent, MockMarkdownComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MarkdownModule.forRoot({
      loader: HttpClient,
      markedOptions: {
        provide: MarkedOptions,
        useFactory: markedOptionsFactory,
      },
    }),
    FormsModule,
    RouterModule,
    NgbModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [ConfigService],
    },
    FeatureEnabledGuard,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

export function markedOptionsFactory(): MarkedOptions {
  const renderer = new MarkedRenderer();

  renderer.blockquote = (text: string) => {
    return '<blockquote class="blockquote"><p>' + text + '</p></blockquote>';
  };

  return {
    renderer: renderer,
    gfm: true,
    tables: true,
    breaks: false,
    pedantic: false,
    sanitize: false,
    smartLists: true,
    smartypants: false,
  };
}
