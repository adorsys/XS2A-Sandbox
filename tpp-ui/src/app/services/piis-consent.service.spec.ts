import { TestBed, waitForAsync } from '@angular/core/testing';

import { PiisConsentService } from './piis-consent.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('PiisConsentService', () => {
  let service: PiisConsentService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PiisConsentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
