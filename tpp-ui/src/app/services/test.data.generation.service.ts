import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class TestDataGenerationService {
    private url = `${environment.staffAccessResourceEndPoint}`;

    constructor(private http: HttpClient) {
    }

    public generateTestData(generatePaymentsFlag: boolean) {
        return this.http.get(this.url + '/generate', {
            params: {
                generatePayments: generatePaymentsFlag === undefined? 'false' : String(generatePaymentsFlag)
            },
            responseType: 'text'
        });
    }

    public generateIban() {
        return this.http.get(environment.tppBackend + '/data/generate/iban', {responseType: "text"});
    }
}
