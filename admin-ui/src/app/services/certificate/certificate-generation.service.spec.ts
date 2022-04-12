/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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

import { CertificateGenerationService } from './certificate-generation.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { environment } from '../../../environments/environment';
import { HttpClientModule } from '@angular/common/http';
import { inject } from '@angular/core';

describe('CertificateGenerationService', () => {
  let httpMock: HttpTestingController;
  let certGenerationService: CertificateGenerationService;

  const url = `${environment.certificateGenerationServer}`;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [CertificateGenerationService],
    });
    httpMock = TestBed.inject(HttpTestingController);
    certGenerationService = TestBed.inject(CertificateGenerationService);
  });

  it('should be created', () => {
    expect(certGenerationService).toBeTruthy();
  });

  it('should load the generate', () => {
    const mockCertData: any = {};
    certGenerationService.generate(mockCertData).subscribe((data: any) => {
      expect(data).toBe('');
    });

    const req = httpMock.expectOne(url + '/api/cert-generator', mockCertData);
    expect(req.request.method).toBe('POST');
    httpMock.verify();
  });
});
