import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class TestDataGenerationService {
    private url = `${environment.tppBackend}/data/generate`;

    constructor(private http: HttpClient) {
    }

    public generateTestData(generatePaymentsFlag: boolean) {
        return this.http.get(this.url, {
            params: {
                generatePayments: generatePaymentsFlag === undefined? 'false' : String(generatePaymentsFlag)
            },
            responseType: 'text'
        });
    }

    public generateIban() {
        return this.http.get(this.url + '/iban', {responseType: "text"});
    }
}
