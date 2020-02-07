import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AcceptType} from '../models/acceptType.model';

@Injectable()
export class RestService {
  serverUrl = '/xs2a-proxy';

  constructor(private http: HttpClient) {
  }

  public sendRequest(
    method,
    url,
    headerParams,
    xml: boolean,
    body?,
    acceptHeader?: string
  ): Observable<any> {
    const headers = this.buildHeadersForRequest(
      headerParams,
      xml,
      acceptHeader
    );

    if (acceptHeader && acceptHeader == AcceptType.xml) {
      const options: {
        headers?: HttpHeaders;
        observe?: 'response';
        responseType: 'text';
      } = {
        observe: 'response',
        headers,
        responseType: 'text',
      };
      return this.sendRequestWithSetOptions(method, url, options, body);

    } else {
      const options: {
        headers?: HttpHeaders;
        observe?: 'response';
        responseType: 'json';
      } = {
        observe: 'response',
        headers,
        responseType: 'json',
      };
      return this.sendRequestWithSetOptions(method, url, options, body);
    }
  }

  private buildHeadersForRequest(
    headerParams: any,
    xml: boolean,
    acceptHeader: string
  ) {
    headerParams['Content-Type'] = xml ? 'application/xml' : 'application/json';
    headerParams.Accept = acceptHeader ? acceptHeader : 'application/json';
    return new HttpHeaders(headerParams);
  }

  private sendRequestWithSetOptions(method, url, options, body?) {
    switch (method) {
      case 'POST':
        return this.http.post(this.serverUrl + '/' + url, body, options);
      case 'GET':
        return this.http.get(this.serverUrl + '/' + url, options);
      case 'PUT':
        return this.http.put(this.serverUrl + '/' + url, body, options);
      case 'DELETE':
        return this.http.delete(this.serverUrl + '/' + url, options);
      default:
        break;
    }
  }
}
