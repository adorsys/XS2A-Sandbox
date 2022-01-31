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

import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { InfoService } from '../commons/info/info.service';

@Injectable()
export class GlobalErrorsHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {}

  public get router(): Router {
    return this.injector.get(Router);
  }

  public get infoService(): InfoService {
    return this.injector.get(InfoService);
  }

  public get activatedRoute(): ActivatedRoute {
    return this.injector.get(ActivatedRoute);
  }

  public handleError(errorObj) {
    console.error('TPP UI error handler: ', errorObj);

    if (errorObj instanceof HttpErrorResponse) {
      this.zone.run(() => {
        let error = errorObj.error;
        let errorMessage = error ? error.message : error.statusText;
        this.infoService.openFeedback(errorMessage, {
          severity: 'error',
        });
      });
    } else {
      this.zone.run(() => {
        let errorMessage = errorObj.message;
        this.infoService.openFeedback(errorMessage, {
          severity: 'info',
        });
      });
    }
  }
}
