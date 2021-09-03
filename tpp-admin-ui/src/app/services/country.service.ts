import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environment/environment';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CountryService {
  public url = `${environment.tppAdminBackend}`;

  constructor(private http: HttpClient) {}

  getCountryCodes(): Observable<Record<string, any>> {
    return this.http.get(this.url + '/codes');
  }

  getCountryList(): Observable<Record<string, any>[]> {
    return this.getCountryCodes().pipe(
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
