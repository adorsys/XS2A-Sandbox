import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PiisConsent, User } from '../models/user.model';
import { AccountAccess } from '../models/account-access.model';
import 'rxjs-compat/add/observable/of';
import { PaginationResponse } from '../models/pagination-reponse';
import { Account } from '../models/account.model';

@Injectable({
  providedIn: 'root',
})
export class PiisConsentService {
  public url = `${environment.tppBackend}/piis-consents`;

  constructor(private http: HttpClient) {}

  getPiisConsent(consentId: string, userLogin: string): Observable<PiisConsent> {
    console.log(this.url + '/' + consentId + '?userLogin=' + userLogin);
    return this.http.get<PiisConsent>(this.url + '/' + consentId + '?userLogin=' + userLogin);
  }

  getPiisConsents(userLogin: string): Observable<PaginationResponse<PiisConsent[]>> {
    return this.http.get<PaginationResponse<PiisConsent[]>>(this.url + '?userLogin=' + userLogin);
  }

  createPiisConsent(piisConsent: PiisConsent, userLogin: string, password: string): Observable<any> {
    console.log(this.url + '?userLogin=' + userLogin + '&password=' + password);
    return this.http.post(this.url + '?userLogin=' + userLogin + '&password=' + password, piisConsent);
  }

  // @PutMapping("/{consentId}/terminate")
  putPiiSConsent(piisConsent: PiisConsent) {
    return this.http.put(this.url + '/' + piisConsent.consentId + '/terminate', piisConsent);
  }
}
