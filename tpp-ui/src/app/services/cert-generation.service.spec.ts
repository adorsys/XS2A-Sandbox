import { TestBed, inject } from '@angular/core/testing';

import { CertGenerationService } from './cert-generation.service';

describe('CertGenerationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CertGenerationService]
    });
  });

  it('should be created', inject([CertGenerationService], (service: CertGenerationService) => {
    expect(service).toBeTruthy();
  }));
});
