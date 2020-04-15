import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { CustomizeService } from './customize.service';

describe('CustomizeService', () => {
  let service: CustomizeService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomizeService],
    });
    service = TestBed.get(CustomizeService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('custom should return boolean', () => {
    expect(typeof service.custom).toBe('boolean');
  });
});
