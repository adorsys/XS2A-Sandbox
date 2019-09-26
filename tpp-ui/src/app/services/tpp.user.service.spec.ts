import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TppUserService } from './tpp.user.service';

describe('Tpp.UserService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule]
  }));

  it('should be created', () => {
    const service: TppUserService = TestBed.get(TppUserService);
    expect(service).toBeTruthy();
  });
});
