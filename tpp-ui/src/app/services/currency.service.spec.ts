import { TestBed } from '@angular/core/testing';

import { CurrencyService } from './currency.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {environment} from "../../environments/environment";

describe('CurrencyService', () => {
  let service: CurrencyService;
  let httpTestingController: HttpTestingController;
  let url = `${environment.tppBackend}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CurrencyService],
    });
    service = TestBed.get(CurrencyService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    const service: CurrencyService = TestBed.get(CurrencyService);
    expect(service).toBeTruthy();
    httpTestingController.expectOne(url + '/currencies');
  });
});
