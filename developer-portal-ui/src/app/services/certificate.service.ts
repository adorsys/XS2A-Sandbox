/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { BehaviorSubject } from 'rxjs';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class CertificateService {
  private certificateKey = 'certificate';

  private default = new BehaviorSubject<boolean>(true);
  currentDefault = this.default.asObservable();

  constructor(private http: HttpClient) {}

  getQwacCertificate(): Observable<any> {
    return this.http.get('./assets/content/certificate.txt', {
      responseType: 'text',
    });
  }

  getStoredCertificate() {
    return LocalStorageService.get(this.certificateKey);
  }

  storeCertificate(certificate) {
    LocalStorageService.set(this.certificateKey, certificate);
  }

  removeCertificate() {
    LocalStorageService.remove(this.certificateKey);
    this.setDefault(true);
  }

  setDefault(value: boolean) {
    this.default.next(value);
  }
}
