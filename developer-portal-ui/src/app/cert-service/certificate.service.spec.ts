import { TestBed } from '@angular/core/testing';
import { CertificateService } from './certificate.service';
import { RouterTestingModule } from '@angular/router/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { PspRole } from '../../models/pspRole';
import { CertificateResponse } from '../../models/certificateResponse';

describe('CertificateService', () => {
  let service: CertificateService;
  let httpMock: HttpTestingController;
  const certResponse: CertificateResponse = {
    encodedCert: '-----BEGIN CERTIFICATE-----BAR-----END CERTIFICATE-----',
    privateKey:
      '-----BEGIN RSA PRIVATE KEY-----FOO-----END RSA PRIVATE KEY-----',
    keyId: '1612748784',
    algorithm: 'SHA256WITHRSA',
  };
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    })
  );

  beforeEach(() => {
    service = TestBed.get(CertificateService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return certResponse', () => {
    service.certResponse = certResponse;
    const givenCertResponse = service.loadCertResponse();
    expect(givenCertResponse).toEqual(service.certResponse);
  });

  it('should save certResponse', () => {
    expect(service.certResponse).not.toEqual(certResponse);
    service.saveCertResponse(certResponse);
    expect(service.certResponse).toEqual(certResponse);
  });

  it('should request generation of certificate and return it', () => {
    const certData = {
      roles: [PspRole.PIS],
      authorizationNumber: '87B2AC',
      countryName: 'Germany',
      domainComponent: 'public.corporation.de',
      localityName: 'Nuremberg',
      organizationName: 'Fictional Corporation AG',
      organizationUnit: 'Information Technology',
      stateOrProvinceName: 'Bayern',
      validity: 365,
    };

    const mockResponse = certResponse;

    service.createCertificate(certData).subscribe(backendResponse => {
      expect(backendResponse.algorithm).toBe('SHA256WITHRSA');
      expect(backendResponse.keyId).toBe('1612748784');
      expect(backendResponse.privateKey).toBe(
        '-----BEGIN RSA PRIVATE KEY-----FOO-----END RSA PRIVATE KEY-----'
      );
      expect(backendResponse.encodedCert).toBe(
        '-----BEGIN CERTIFICATE-----BAR-----END CERTIFICATE-----'
      );
    });

    const req = httpMock.expectOne(`${service.CREATE_CERT_URL}`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
