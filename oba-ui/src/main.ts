/// <reference types="@angular/localize" />

/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { URL_PARAMS_PROVIDER } from './app/common/constants/constants';
import { environment } from './environments/environment';

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

platformBrowserDynamic([
  {
    provide: URL_PARAMS_PROVIDER,
    useValue: getUrlParams(),
  },
])
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));
