import { Injectable } from '@angular/core';
import {PSUPISService} from "../../api/services";
import {Observable} from "rxjs";
import {AuthorizeResponse} from "../../api/models/authorize-response";
import PisAuthUsingGETParams = PSUPISService.PisAuthUsingGETParams;

@Injectable({
  providedIn: 'root'
})
export class PisService {

  constructor(private pisService: PSUPISService) { }

  public pisAuthCode(params: PisAuthUsingGETParams): Observable<AuthorizeResponse> {
    return this.pisService.pisAuthUsingGET(params);
  }
}
