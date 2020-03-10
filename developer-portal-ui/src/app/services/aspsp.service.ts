import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AspspService {
  aspspProfileUri = '/aspsp-proxy/api/v1/aspsp-profile';

  constructor(private http: HttpClient) {}

  getAspspProfile(): Observable<any> {
    return this.http.get(this.aspspProfileUri);
  }

  getScaApproaches(): Observable<any> {
    return this.http.get(this.aspspProfileUri + '/sca-approaches');
  }
}
