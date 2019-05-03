import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class RestService {
  constructor(private http: HttpClient) {}
  // serverUrl = 'http://localhost:8088';
  serverUrl = 'http://localhost:8089/v1/consents';

  public postRequest(body, headerParams): Observable<any> {
    const headers = new HttpHeaders(headerParams);
    console.log('body:', body);
    console.log('headers:', headers);
    return this.http.post(this.serverUrl, body, { headers });
  }
}
