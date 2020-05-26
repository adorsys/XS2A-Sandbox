import { HttpClientModule } from '@angular/common/http';
import { TestBed, inject } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestDataGenerationService } from './test.data.generation.service';
import { environment } from '../../environments/environment';

describe('TestDataGenerationService', () => {
  let httpMock: HttpTestingController;
  let testDataGenerationService: TestDataGenerationService;
  let url = `${environment.tppBackend}`;
  let userBranch = '';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TestDataGenerationService],
    });
    testDataGenerationService = TestBed.get(TestDataGenerationService);
    httpMock = TestBed.get(HttpTestingController);
  });
  it('should be created', () => {
    const service = TestBed.get(TestDataGenerationService);
    expect(service).toBeTruthy();
  });

  it('should get a generate Iban', () => {
    testDataGenerationService.generateIban(userBranch);
    httpMock.verify();
  });

  it('should generate the example Test Data', () => {
    testDataGenerationService
      .generateExampleTestData('accountId')
      .subscribe((data: any) => {
        expect(data).toBe('accountId');
      });
    const req = httpMock.expectOne(url + 'accountId');
    expect(req.request.method).toBe('GET');
    req.flush('accountId');
    httpMock.verify();
  });

  it('should generate the Test Data', () => {
    testDataGenerationService.generateTestData('EUR', true);
    httpMock.verify();
  });
});
