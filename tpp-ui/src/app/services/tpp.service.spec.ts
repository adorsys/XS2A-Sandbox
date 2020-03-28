import {HttpClientModule} from '@angular/common/http';
import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {environment} from '../../environments/environment';
import { TppService } from '../services/tpp.service';

describe('TppService', () => {

    let httpMock: HttpTestingController;
    let tppService: TppService;
    const url = `${environment.tppBackend}`;
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [TppService]
        });
        tppService = TestBed.get(TppService);
        httpMock = TestBed.get(HttpTestingController);
    })

    it('should delete the Tpp user', () => {
        tppService.deleteTpp();
    });

    it('should delete the accountTransations ', () => {
        tppService.deleteAccountTransations('accountId').subscribe((data: any) => {
            expect(data).toBe('accountId');});
        const req = httpMock.expectOne(url + /account/ + 'accountId');
        expect(req.request.method).toBe('DELETE');
        req.flush('accountId');
        httpMock.verify();
    });
});
