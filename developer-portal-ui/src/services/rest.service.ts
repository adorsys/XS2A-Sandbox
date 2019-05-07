import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class RestService {
  constructor(private http: HttpClient) {}
  // serverUrl = 'https://xs2a.integ.cloud.adorsys.de/v1/consents';
  serverUrl = 'http://localhost:8089/';

  public sendRequest(method, url, body?, headerParams?): Observable<any> {
    switch (method) {
      case 'POST':
        return this.http.post(this.serverUrl + url, body, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'GET':
      // TODO return get request here
      case 'PUT':
      // TODO return put request here
      case 'DELETE':
      // TODO return delete request here
      default:
        break;
    }
  }
}
