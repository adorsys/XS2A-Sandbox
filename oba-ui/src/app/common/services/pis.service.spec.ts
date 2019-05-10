import { TestBed } from '@angular/core/testing';

import { PisService } from './pis.service';

describe('PisService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PisService = TestBed.get(PisService);
    expect(service).toBeTruthy();
  });
});
