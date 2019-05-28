import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class RestService {
  // serverUrl = 'https://xs2a.integ.cloud.adorsys.de/v1/consents';
  serverUrl = '/xs2a-proxy/';

  constructor(private http: HttpClient) {}

  public sendRequest(method, url, headerParams, body?): Observable<any> {
    switch (method) {
      case 'POST':
        return this.http.post(this.serverUrl + url, body, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'GET':
        return this.http.get(this.serverUrl + url, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'PUT':
        return this.http.put(this.serverUrl + url, body, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'DELETE':
        return this.http.delete(this.serverUrl + url, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      default:
        break;
    }
  }
}
