import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { environment } from './environments/environment';
import { AppModule } from './app/app.module';
import browser from 'browser-detect';

if (environment.production) {
  enableProdMode();
}
const result = browser();
if (result.name === 'chrome' || result.name === 'firefox' || result.name === 'safari' || result.name === 'edge') {
  platformBrowserDynamic()
    .bootstrapModule(AppModule)
    .catch((err) => console.error(err));
} else {
  document.getElementById('unsupportedBrowser').innerHTML =
    '<p class="lead">We officially support <mark>Google Chrome, Mozilla Firefox and Microsoft Egde latest versions</mark>.<br><br>Please access us using these browsers only.</p>';
}
