import { TestBed } from '@angular/core/testing';

import { UrlLoadService } from './url-load.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('UrlLoadService', () => {
  let service: UrlLoadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(UrlLoadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
