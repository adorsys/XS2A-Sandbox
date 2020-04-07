import {HttpClientModule} from '@angular/common/http';
import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TppUserService} from './tpp.user.service';
import {environment} from '../../environments/environment';

describe('TppUserService', () => {
    let httpTestingController: HttpTestingController;
    let tppUserService: TppUserService;
    const getUrl = `${environment.tppBackend + '/users/me'}`;
    const url = `${environment.tppBackend + '/users'}`;

  beforeEach(() => TestBed.configureTestingModule({
      imports: [
          HttpClientModule,
          HttpClientTestingModule,
      ],
      providers: [TppUserService]
  }));

    it('should be created', () => {
    const service: TppUserService = TestBed.get(TppUserService);
    expect(service).toBeTruthy();
  });
});
