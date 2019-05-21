import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class RestService {
  constructor(private http: HttpClient) {}
  // serverUrl = 'https://xs2a.integ.cloud.adorsys.de/v1/consents';
  serverUrl = 'http://localhost:8089/';

  public sendRequest(
    method,
    url,
    body?,
    headerParams?,
    params?
  ): Observable<any> {
    switch (method) {
      case 'POST':
        return this.http.post(this.serverUrl + url, body, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'GET':
        return this.http.get(this.serverUrl + `${url}/${params}`, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'PUT':
        return this.http.put(this.serverUrl + url, body, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      case 'DELETE':
        return this.http.delete(this.serverUrl + `${url}/${params}`, {
          observe: 'response',
          headers: new HttpHeaders(headerParams),
        });
      default:
        break;
    }
  }
}
