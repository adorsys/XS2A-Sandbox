import { TestBed, inject } from '@angular/core/testing';

import { RestService } from './rest.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('RestService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        RestService
      ]
    });
  });

  it('should be created (not all)', inject([RestService], (service: RestService) => {
    expect(service).toBeTruthy();
  }));
});
