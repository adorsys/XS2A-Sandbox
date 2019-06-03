import {ErrorHandler, Injectable, Injector, NgZone} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import {InfoService} from "../commons/info/info.service";

@Injectable()
export class GlobalErrorsHandler implements ErrorHandler {

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

    public handleError(error: HttpErrorResponse) {
        console.error("TPP UI error handler: ", error);

        let httpErrorCode = error.status;

        console.log(httpErrorCode);

        this.zone.run(() => {

            // default error handling
            switch (httpErrorCode) {
                case 401: {
                    this.infoService.openFeedback('Invalid authentication credentials', {
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
                case 404: {
                    this.infoService.openFeedback('Required resource not found', {
                        severity: 'error'
                    });
                    break;
                }
                case 409: {
                    this.infoService.openFeedback('Conflict. A record with the given email or login already exists.', {
                        severity: 'error'
                    });
                    break;
                }
                default: {
                    // if required could be redirected to internal server error page
                    this.infoService.openFeedback('Internal server error. Please contact technical support', {
                        severity: 'error'
                    });
                    break;
                }
            }

        });

    }

}
