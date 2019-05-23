import {ErrorHandler, Injectable, Injector, NgZone} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {InfoService} from "../info/info.service";
import {HttpErrorResponse} from "@angular/common/http";
import {RoutingPath} from "../models/routing-path.model";

@Injectable()
export class ObaErrorsHandler implements ErrorHandler {
  private encryptedConsentId: string;
  private paymentId: string;
  private redirectId: string;

  constructor(
    private zone: NgZone,
    private infoService: InfoService,
    private injector: Injector) {
  }

  public get router(): Router {
    return this.injector.get(Router);
  }

  public get activatedRoute(): ActivatedRoute {
    return this.injector.get(ActivatedRoute);
  }

  public handleError(error: HttpErrorResponse) {
    console.error(error);

    let httpErrorCode = error.status;

    // if router is on the login page: special error handling
    const login = this.router.url.indexOf(`/${RoutingPath.LOGIN}`) > 0;
    if (login) {

      this.activatedRoute.queryParams.subscribe(params => {
        this.encryptedConsentId = params['encryptedConsentId'];
        this.paymentId = params['paymentId'];
        this.redirectId = params['redirectId'];
      });

      // AIS: if no redirectId or encryptedConsentId is provided
      if (this.encryptedConsentId === undefined || this.redirectId === undefined
        && (this.router.url.indexOf(`${RoutingPath.ACCOUNT_INFORMATION}`) > 0)) {

        this.infoService.openFeedback('No consent data is provided', {
          severity: 'error'
        });
        return;
      }

      // PIS and Payment Cancellation: if no redirectId or paymentId is provided
      if ((this.paymentId === undefined || this.redirectId === undefined)
        && (this.router.url.indexOf(`${RoutingPath.PAYMENT_INITIATION}`) > 0 || this.router.url.indexOf(`${RoutingPath.PAYMENT_CANCELLATION}`) > 0)) {

        this.infoService.openFeedback('No payment data is provided', {
          severity: 'error'
        });
        return;
      }
    }

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
        this.infoService.openFeedback('Consent data is not valid. Please try again', {
          severity: 'error'
        });
        break;
      }
    }

  }

}
