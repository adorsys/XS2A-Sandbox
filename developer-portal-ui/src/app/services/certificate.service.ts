import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/internal/Observable';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class CertificateService {
  private certificateKey = 'certificate';

  private default = new BehaviorSubject<boolean>(true);
  currentDefault = this.default.asObservable();

  constructor(private http: HttpClient) {
  }

  getQwacCertificate(): Observable<any> {
    return this.http.get('./assets/content/certificate.txt', {responseType: 'text'});
  }

  getStoredCertificate() {
    return localStorage.getItem(this.certificateKey);
  }

  storeCertificate(certificate) {
    localStorage.setItem(this.certificateKey, certificate);
  }

  removeCertificate() {
    localStorage.removeItem(this.certificateKey);
    this.setDefault(true);
  }

  setDefault(value: boolean) {
    this.default.next(value);
  }
}
