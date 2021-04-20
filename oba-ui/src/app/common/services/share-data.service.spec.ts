import { TestBed, waitForAsync } from '@angular/core/testing';
import { ShareDataService } from './share-data.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ShareDataService', () => {
  let shareDataService: ShareDataService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [ShareDataService],
      });
      shareDataService = TestBed.inject(ShareDataService);
    })
  );

  it('should be created', () => {
    const service: ShareDataService = TestBed.inject(ShareDataService);
    expect(service).toBeTruthy();
  });
});
