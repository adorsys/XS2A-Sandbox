import { TestBed } from '@angular/core/testing';

import { TrackingIdService } from './tracking-id.service';

describe('TrackingIdService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TrackingIdService = TestBed.get(TrackingIdService);
    expect(service).toBeTruthy();
  });
});
