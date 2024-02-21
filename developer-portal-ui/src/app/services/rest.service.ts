/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AcceptType } from '../models/acceptType.model';

@Injectable()
export class RestService {
  serverUrl = '/xs2a-proxy';

  constructor(private http: HttpClient) {}

  public sendRequest(method, url, headers, acceptHeader: string, body?): Observable<any> {
    if (acceptHeader && acceptHeader === AcceptType.xml) {
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
