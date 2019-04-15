import {ErrorHandler, Injectable, Injector, NgZone} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";

@Injectable()
export class ObaErrorsHandler implements ErrorHandler {
  constructor(
    private zone: NgZone,
    private injector: Injector) {
  }

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
    });
  }

}
