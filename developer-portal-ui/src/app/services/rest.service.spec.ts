import { TestBed } from '@angular/core/testing';

import { RestService } from './rest.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('RestService', () => {
  let service: RestService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RestService],
    });
    service = TestBed.inject(RestService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
