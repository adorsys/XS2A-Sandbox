import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { OnlineBankingService } from './online-banking.service';

describe('OnlineBankingService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    })
  );

  it('should be created', () => {
    const service: OnlineBankingService = TestBed.inject(OnlineBankingService);
    expect(service).toBeTruthy();
  });
});
