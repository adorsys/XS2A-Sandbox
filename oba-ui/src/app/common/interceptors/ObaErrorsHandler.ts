import {ErrorHandler, Injectable, Injector, NgZone} from "@angular/core";
import {UNAUTHORIZED, BAD_REQUEST, FORBIDDEN} from "http-status-codes";
import {ActivatedRoute, Router} from "@angular/router";

@Injectable()
export class ObaErrorsHandler implements ErrorHandler {
  constructor(
    private zone: NgZone,
    private injector: Injector) {}

  public get router(): Router {
    return this.injector.get(Router);
  }

  public get activatedRoute(): ActivatedRoute {
    return this.injector.get(ActivatedRoute);
  }

  public handleError(error: any) {
    console.error(error);
    let httpErrorCode = error.httpErrorCode;
    this.activatedRoute.queryParams.subscribe(params => {
      console.log(params);
      switch (httpErrorCode) {
        case UNAUTHORIZED:
          this.zone.run(() => this.router.navigate(['result'], {queryParams: params}));
          break;
        case FORBIDDEN:
          this.zone.run(() => this.router.navigate(['result'], {queryParams: params}));
          break;
        case BAD_REQUEST:
          this.zone.run(() => this.router.navigate(['result'], {queryParams: params}));
          break;
        default:
          this.zone.run(() => this.router.navigate(['result'], {queryParams: params}));
      }
    });
  }

}
