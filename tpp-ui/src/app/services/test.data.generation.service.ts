import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class TestDataGenerationService {
    private baseUrl = `${environment.tppBackend}`;

    constructor(private http: HttpClient) {
    }

    public generateTestData(generatePaymentsFlag: boolean, url?: string) {
        if (url) {
            return this.http.get(this.baseUrl + url, {
                responseType: 'text'
            });
        }
        return this.http.get(this.baseUrl + '/data/generate/', {
            params: {
                generatePayments: generatePaymentsFlag === undefined? 'false' : String(generatePaymentsFlag)
            },
            responseType: 'text'
        });
    }

    public generateIban() {
        return this.http.get(this.baseUrl + '/data/generate/iban', {responseType: "text"});
    }
}
