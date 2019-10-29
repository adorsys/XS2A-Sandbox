import { TestBed } from '@angular/core/testing';

import { AspspService } from './aspsp.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('AspspService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    })
  );

  it('should be created', () => {
    const service: AspspService = TestBed.get(AspspService);
    expect(service).toBeTruthy();
  });
});
