import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CertificateGenerationService {

  url = `${environment.certificateGenerationServer}`;

  constructor(private http: HttpClient) {
  }

  public generate(certData: any) {
    return this.http.post(`${this.url}/api/cert-generator`, certData);
  }
}
