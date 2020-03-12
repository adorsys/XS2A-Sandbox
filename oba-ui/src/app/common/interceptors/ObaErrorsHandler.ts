import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { InfoService } from '../info/info.service';

@Injectable()
export class ObaErrorsHandler implements ErrorHandler {

  constructor(
    private zone: NgZone,
    private injector: Injector) {
  }

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
      console.error("OBA UI error handler: ", errorObj);
      if (errorObj instanceof HttpErrorResponse) {
          this.zone.run(() => {
              let error = errorObj.error;
              let errorMessage = error ? error.message : error.statusText;
              this.infoService.openFeedback(errorMessage, {
                  severity: 'error'
              });
          });
      } else {
          this.zone.run(() => {
              let errorMessage = errorObj.message;
              this.infoService.openFeedback(errorMessage, {
                  severity: 'info'
              });
          });
      }
  }
}
