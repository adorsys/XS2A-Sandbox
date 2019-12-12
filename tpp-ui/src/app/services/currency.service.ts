import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  private currencies;
  public url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {
    this.initializeCurrenciesList();
  }

  initializeCurrenciesList() {
    return this.getSupportedCurrencies().subscribe(
      data => this.currencies = data,
      error => console.log(error)
    )
  }

  get currencyList() {
    return this.currencies;
  }

  getSupportedCurrencies() {
    return this.http.get(this.url + '/currencies');
  }

}
