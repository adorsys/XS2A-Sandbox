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

import { TestBed } from '@angular/core/testing';

import { CertificateDownloadService } from './certificate-download.service';
import { of } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CertificateGenerationService } from './certificate-generation.service';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../commons/info/info.module';

describe('CertificateDownloadService', () => {
  let service: CertificateDownloadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule, RouterTestingModule, InfoModule],
      providers: [CertificateGenerationService, CertificateDownloadService],
    });
    service = TestBed.inject(CertificateDownloadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a zip Url', (done) => {
    const encodedCert = 'encodedCert';
    const privateKey = 'privateKey';
    spyOn(CertificateDownloadService, 'generateZipFile').and.returnValue(of({}).toPromise());
    spyOn(CertificateDownloadService, 'createObjectUrl').and.returnValue('dummy-obj-val');
    service.createZipUrl(encodedCert, privateKey).then((r) => {
      expect(r).toBe('dummy-obj-val');
      done();
    });
  });

  it('createObjectUrl', () => {
    const zip = 'dummy-zip-content';
    const mockWindow = {
      URL: {
        createObjectURL: (param) => `url-string-object-${param}`,
      },
    };
    const result = CertificateDownloadService.createObjectUrl(zip, mockWindow);
    expect(result).toBe('url-string-object-dummy-zip-content');
  });

  it('should generate a Zip file', (done) => {
    const certBlob = 'certBlob';
    const keyBlob = 'keyBlob';
    CertificateDownloadService.generateZipFile(certBlob, keyBlob).then((r: any) => {
      expect(r).toBeTruthy();
      done();
    });
  });
});
