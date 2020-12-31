import {TestBed} from '@angular/core/testing';

import {CertificateGenerationService} from './certificate-generation.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {environment} from '../../../environments/environment';
import {HttpClientModule} from '@angular/common/http';
import {inject} from '@angular/core';

describe('CertificateGenerationService', () => {
  let httpMock: HttpTestingController;
  let certGenerationService: CertificateGenerationService;

  const url = `${environment.certificateGenerationServer}`;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule
      ],
      providers: [CertificateGenerationService]
    });
    httpMock = TestBed.get(HttpTestingController);
    certGenerationService = TestBed.get(CertificateGenerationService);
  });

  it('should be created', () => {
    expect(certGenerationService).toBeTruthy();
  });

  it('should load the generate', () => {
    let mockCertData: any = {};
    certGenerationService.generate(mockCertData).subscribe((data: any) => {
      expect(data).toBe('');
    });

    const req = httpMock.expectOne(url + '/api/cert-generator', mockCertData);
    expect(req.request.method).toBe('POST');
    httpMock.verify();
  });

});
