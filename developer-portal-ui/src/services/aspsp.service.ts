import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AspspService {
  aspspProfileUri = '/aspsp-proxy';

  constructor(private http: HttpClient) {}

  getAspspProfile(): Observable<any> {
    return this.http.get(this.aspspProfileUri + 'api/v1/aspsp-profile');
  }
}
