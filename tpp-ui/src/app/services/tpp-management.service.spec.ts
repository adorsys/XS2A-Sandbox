import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {environment} from '../../environments/environment';
import {TppManagementService} from './tpp-management.service';

describe('TppService', () => {

  let httpMock: HttpTestingController;
  let tppService: TppManagementService;
  const url = `${environment.tppBackend}`;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TppManagementService]
    });
    tppService = TestBed.get(TppManagementService);
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should delete the Tpp user', () => {
    tppService.deleteSelf();
  });

  it('should delete the accountTransations ', () => {
    tppService.deleteAccountTransactions('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + /account/ + 'accountId');
    expect(req.request.method).toBe('DELETE');
    req.flush('accountId');
    httpMock.verify();
  });
});
