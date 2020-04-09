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
