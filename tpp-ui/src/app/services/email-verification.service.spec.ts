import { TestBed } from '@angular/core/testing';

import { EmailVerificationService } from './email-verification.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('EmailVerificationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule]
  }));
  it('should be created', () => {
    const service: EmailVerificationService = TestBed.get(EmailVerificationService);
    expect(service).toBeTruthy();
  });
});
