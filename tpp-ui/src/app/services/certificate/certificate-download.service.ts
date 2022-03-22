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

import { Injectable } from '@angular/core';
import JSZip from 'jszip';
import { CertificateGenerationService } from './certificate-generation.service';
import { Router } from '@angular/router';
import { InfoService } from '../../commons/info/info.service';

@Injectable({
  providedIn: 'root',
})
export class CertificateDownloadService {
  constructor(
    private certificateGenerationService: CertificateGenerationService,
    private router: Router,
    private infoService: InfoService
  ) {}

  generateAndDownloadCertificate(certificate, message: string) {
    if (certificate) {
      this.certificateGenerationService.generate(certificate).subscribe(
        (data: any) => {
          if (data) {
            const encodedCert = data.encodedCert;
            const privateKey = data.privateKey;

            this.createZipUrl(encodedCert, privateKey).then((url) => {
              this.navigateAndGiveFeedback({ message: message, url: url });
            });
          } else {
            this.infoService.openFeedback('No certificate was generated, try again.');
          }
        },
        (error) => {
          this.infoService.openFeedback(error.error.message);
        }
      );
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

  navigateAndGiveFeedback(options: { navigateUrl?: string; message?: string; url?: string }) {
    this.infoService.openFeedback(options.message);
    setTimeout(() => CertificateDownloadService.downloadFile(options.url), 2000);

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
    return zip.generateAsync({ type: 'blob' });
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
