import { TestBed } from '@angular/core/testing';

import { ResetLedgersService } from './reset-ledgers.service';

describe('ResetLedgersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ResetLedgersService = TestBed.get(ResetLedgersService);
    expect(service).toBeTruthy();
  });
});
