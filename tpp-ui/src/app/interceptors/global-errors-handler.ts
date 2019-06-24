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

    public handleError(errorResponse: HttpErrorResponse) {
        console.error("TPP UI error handler: ", errorResponse);

        this.zone.run(() => {
            let error = errorResponse.error;
            let errorMessage = error?error.message:errorResponse.statusText;
            this.infoService.openFeedback(errorMessage, {
                severity: 'error'
            });
        });
    }

}
