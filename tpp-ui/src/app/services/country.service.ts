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

import { Injectable, OnInit } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CountryService {
  public url = `${environment.tppBackend}`;
  private countries = new BehaviorSubject<any>(null);
  currentCountries = this.countries.asObservable();

  constructor(private http: HttpClient) {}

  getCountryCodes(): Observable<any> {
    return this.http.get(this.url + '/codes');
  }

  getCountryByTppId(countries: any, tppId: string): string {
    if (countries && tppId) {
      return countries[tppId.slice(0, 2)];
    }
    return '';
  }

  loadCountries() {
    this.getCountryCodes()
      .toPromise()
      .then((data) => {
        this.countries.next(data);
      });
  }

  getCountryList(): Observable<Array<any>> {
    return this.currentCountries.pipe(
      map((data) => {
        if (data != null) {
          const countries = [];
          Object.keys(data).forEach((countryKey: string) => {
            countries.push({ code: countryKey, name: data[countryKey] });
          });

          return countries;
        }
      })
    );
  }
}
