import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  public url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {
  }

  getSupportedCurrencies() {
    return this.http.get(this.url + '/currencies');
  }

}
