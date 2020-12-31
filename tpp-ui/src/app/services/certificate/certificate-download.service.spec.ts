import { TestBed } from '@angular/core/testing';
import JSZip from 'jszip';

import { CertificateDownloadService } from './certificate-download.service';
import {of} from 'rxjs';
import {HttpClientModule} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CertificateGenerationService} from './certificate-generation.service';
import {RouterTestingModule} from '@angular/router/testing';
import {InfoModule} from '../../commons/info/info.module';

describe('CertificateDownloadService', () => {
  let service: CertificateDownloadService;

  beforeEach(() =>  {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        RouterTestingModule,
        InfoModule,
      ],
      providers: [
        CertificateGenerationService,
      CertificateDownloadService
      ]
    });
    service = TestBed.get(CertificateGenerationService);
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
    const mockZip: JSZip = ['Blob'];
    CertificateDownloadService.generateZipFile(certBlob, keyBlob).then((r: any) => {
      expect(r).toBeTruthy();
      done();
    });
  });
});
