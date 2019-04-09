import { TestBed } from '@angular/core/testing';

import { PisCancellationService } from './pis-cancellation.service';

describe('PisCancellationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PisCancellationService = TestBed.get(PisCancellationService);
    expect(service).toBeTruthy();
  });
});
