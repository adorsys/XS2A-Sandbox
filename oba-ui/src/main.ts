import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { URL_PARAMS_PROVIDER } from './app/common/constants/constants';
import { environment } from './environments/environment';
import browser from 'browser-detect';

if (environment.production) {
  enableProdMode();
}

const getUrlParams = (): any => {
  const params = {};
  const url = window.location.href;
  const questionMarkPosition = url.indexOf('?');
  if (questionMarkPosition > -1) {
    const queryString = url.substr(questionMarkPosition + 1);
    let part;
    const queries = queryString.split('&');
    for (let i = 0, length = queries.length; i < length; i++) {
      const queryPair = queries[i];
      part = queryPair.split(/=(.+)/);
      params[part[0]] = part[1];
    }
  }
  return params;
};
const result = browser();
if (
  result.name === 'chrome' ||
  result.name === 'firefox' ||
  result.name === 'safari' ||
  result.name === ''
) {
  platformBrowserDynamic([
    {
      provide: URL_PARAMS_PROVIDER,
      useValue: getUrlParams(),
    },
  ])
    .bootstrapModule(AppModule)
    .catch((err) => console.error(err));
} else {
  document.getElementById('unsupportedBrowser').innerHTML =
    '<p class="lead">We officially support <mark>Google Chrome, Mozilla Firefox and Microsoft Egde latest versions</mark>.<br><br>Please access us using these browsers only.</p>';
}
