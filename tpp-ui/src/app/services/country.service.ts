import {Injectable, OnInit} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CountryService {
  public url = `${environment.tppBackend}`;
  private countries = new BehaviorSubject<any>(null);
  currentCountries = this.countries.asObservable();

  constructor(private http: HttpClient) {
  }

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
    this.getCountryCodes().toPromise().then(data => {
      this.countries.next(data);
    });
  }

  getCountryList(): Observable<Array<object>> {
    return this.currentCountries.pipe(
      map(
        data => {
          if (data != null) {
            let countries = [];
            Object.keys(data).forEach((countryKey: string) => {
              countries.push({code: countryKey, name: data[countryKey]});
            });

            return countries;
          }
        }
      ));
  }

}
