import {TestBed} from "@angular/core/testing";
import {OnlineBankingService} from "./online-banking.service";
import {RouterTestingModule} from "@angular/router/testing";

describe('OnlineBankingService', () => {
    beforeEach(() => TestBed.configureTestingModule({
        imports: [RouterTestingModule]
    }));

    it('should be created', () => {
        const service: OnlineBankingService = TestBed.get(OnlineBankingService);
        expect(service).toBeTruthy();
    });

});
