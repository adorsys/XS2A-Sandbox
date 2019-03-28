import {ErrorHandler, Injectable, Injector, NgZone} from "@angular/core";
import {UNAUTHORIZED, BAD_REQUEST, FORBIDDEN} from "http-status-codes";
import {Router} from "@angular/router";

@Injectable()
export class ObaErrorsHandler implements ErrorHandler {
  constructor(
    private zone: NgZone,
    private injector: Injector) {}

  public get router(): Router {
    return this.injector.get(Router);
  }

  public handleError(error: any) {
    console.error(error);
    let httpErrorCode = error.httpErrorCode;

    switch (httpErrorCode) {
      case UNAUTHORIZED:
        this.zone.run(() => this.router.navigate(['result']));
        break;
      case FORBIDDEN:
        this.zone.run(() => this.router.navigate(['result']));
        // this.router.navigate(['result']);
        break;
      case BAD_REQUEST:
        this.zone.run(() => this.router.navigate(['result']));
        // this.router.navigate(['result']);
        break;
      default:
        this.zone.run(() => this.router.navigate(['result']));
        // this.router.navigate(['result']);
    }
  }

}
