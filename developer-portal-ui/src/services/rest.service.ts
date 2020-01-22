import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class RestService {
  serverUrl = '/xs2a-proxy';

  constructor(private http: HttpClient) {}

  public sendRequest(
    method,
    url,
    headerParams,
    xml: boolean,
    body?
  ): Observable<any> {
    const headers = this.buildHeadersForRequest(headerParams, xml);

    switch (method) {
      case 'POST':
        return this.http.post(this.serverUrl + '/' + url, body, {
          observe: 'response',
          headers,
        });
      case 'GET':
        return this.http.get(this.serverUrl + '/' + url, {
          observe: 'response',
          headers,
        });
      case 'PUT':
        return this.http.put(this.serverUrl + '/' + url, body, {
          observe: 'response',
          headers,
        });
      case 'DELETE':
        return this.http.delete(this.serverUrl + '/' + url, {
          observe: 'response',
          headers,
        });
      default:
        break;
    }
  }

  private buildHeadersForRequest(headerParams: any, xml: boolean) {
    headerParams['Content-Type'] = xml ? 'application/xml' : 'application/json';
    return new HttpHeaders(headerParams);
  }
}
