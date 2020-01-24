import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class EmailVerificationService {

  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {
  }

  sendEmailForVerification(email: string) {
    return this.http.post(this.url + '/sca', null, {
      params: {email: email}
    });
  }
}
