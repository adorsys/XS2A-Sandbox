import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {SettingsHttpService} from "./settings-http.service";

describe('TrackingIdHttpService', () => {
  let httpTestingController: HttpTestingController;
  let service: SettingsHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        SettingsHttpService
      ]
    });
    service = TestBed.get(SettingsHttpService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
