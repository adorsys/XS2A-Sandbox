import {Injectable} from '@angular/core';
import JSZip from 'jszip';
import {CertificateGenerationService} from './certificate-generation.service';
import {Router} from '@angular/router';
import {InfoService} from '../../commons/info/info.service';

@Injectable({
  providedIn: 'root'
})
export class CertificateDownloadService {

  constructor(private certificateGenerationService: CertificateGenerationService,
              private router: Router,
              private infoService: InfoService) {
  }

  generateAndDownloadCertificate(certificate, message: string) {
    if (certificate) {
      this.certificateGenerationService.generate(certificate).subscribe(
        (data: any) => {
          if (data) {
            const encodedCert = data.encodedCert;
            const privateKey = data.privateKey;

            this.createZipUrl(encodedCert, privateKey).then((url) => {
              this.navigateAndGiveFeedback({message: message, url: url});
            });
          } else {
            this.infoService.openFeedback("No certificate was generated, try again.");
          }
        },
        (error) => {
          this.infoService.openFeedback(error.error.message);
        });
    }
  }

  createZipUrl(encodedCert: string, privateKey: string): Promise<string> {
    const blobCert = new Blob([encodedCert], {
      type: 'text/plain',
    });
    const blobKey = new Blob([privateKey], {
      type: 'text/plain',
    });
    return CertificateDownloadService.generateZipFile(blobCert, blobKey).then((zip) => {
      return CertificateDownloadService.createObjectUrl(zip, window);
    });
  }

  navigateAndGiveFeedback(options: {navigateUrl?: string, message?: string, url?: string}) {
    this.infoService.openFeedback(options.message);
    setTimeout(
      () => CertificateDownloadService.downloadFile(options.url),
      2000
    );

    if (options.navigateUrl) {
      this.router.navigate([options.navigateUrl]);
    }
  }

  static createObjectUrl(zip: any, window: any): string {
    return window.URL.createObjectURL(zip);
  }

  static generateZipFile(certBlob, keyBlob): Promise<any> {
    const zip = new JSZip();
    zip.file('certificate.pem', certBlob);
    zip.file('private.key', keyBlob);
    return zip.generateAsync({type: 'blob'});
  }

  static downloadFile(url: string) {
    const element = document.createElement('a');
    element.setAttribute('href', url);
    element.setAttribute('download', 'tpp_cert.zip');
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }
}
