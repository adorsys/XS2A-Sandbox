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

  public handleError(errorObj: HttpErrorResponse) {
    console.error('OBA error handler: ', errorObj);

    const httpErrorCode = errorObj.status;

    this.zone.run( () => {
      const error = errorObj.error;
      const errorMessage = error ? error.message : error.statusText;

      // default error handling
      switch (httpErrorCode) {
        case 401: {
          this.infoService.openFeedback('Invalid credentials', {
            severity: 'error'
          });
          break;
        }
        case 403: {
          this.infoService.openFeedback('You have no rights to execute this action', {
            severity: 'error'
          });
          break;
        }
        default: {
          // if required could be redirected to internal server error page
          this.infoService.openFeedback(errorMessage, {
            severity: 'error',
            duration: 3000
          });
          break;
        }
      }

    });

  }

}
