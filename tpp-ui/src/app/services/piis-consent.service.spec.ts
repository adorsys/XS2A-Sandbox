import { TestBed } from '@angular/core/testing';

import { PiisConsentService } from './piis-consent.service';

describe('PiisConsentService', () => {
  let service: PiisConsentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PiisConsentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
