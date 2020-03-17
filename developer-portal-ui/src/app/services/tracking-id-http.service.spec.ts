import {TestBed} from '@angular/core/testing';
import {TrackingIdHttpService} from './tracking-id-http.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('TrackingIdHttpService', () => {
  let httpTestingController: HttpTestingController;
  let service: TrackingIdHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        TrackingIdHttpService
      ]
    });
    service = TestBed.get(TrackingIdHttpService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
