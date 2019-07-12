import { TestBed, inject } from '@angular/core/testing';

import { AlertService } from './alert.service';
import {RouterTestingModule} from '@angular/router/testing';

describe('AlertService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AlertService
      ],
      imports: [
        RouterTestingModule
      ]
    });
  });

  it('should be created (unused)', inject([AlertService], (service: AlertService) => {
    expect(service).toBeTruthy();
  }));
});
