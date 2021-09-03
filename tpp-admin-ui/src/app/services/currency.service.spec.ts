import { TestBed } from '@angular/core/testing';

import { CurrencyService } from './currency.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { environment } from '@environment/environment';

describe('CurrencyService', () => {
  let currencyService: CurrencyService;
  let httpMock: HttpTestingController;
  const url = `${environment.tppAdminBackend}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CurrencyService],
    });
    currencyService = TestBed.get(CurrencyService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    const currencyService: CurrencyService = TestBed.get(CurrencyService);
    expect(currencyService).toBeTruthy();
  });

  it('should get supported Currencies', () => {
    currencyService.getSupportedCurrencies();
    httpMock.verify();
  });
});
