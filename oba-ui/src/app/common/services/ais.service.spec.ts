import { TestBed } from '@angular/core/testing';

import { AisService } from './ais.service';

describe('AisService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AisService = TestBed.get(AisService);
    expect(service).toBeTruthy();
  });
});
