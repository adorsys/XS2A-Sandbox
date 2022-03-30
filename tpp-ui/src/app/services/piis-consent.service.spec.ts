import { TestBed, waitForAsync } from '@angular/core/testing';

import { PiisConsentService } from './piis-consent.service';
import { ReactiveFormsModule } from '@angular/forms';
import { InfoModule } from '../commons/info/info.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { IconModule } from '../commons/icon/icon.module';
import { UserService } from './user.service';
import { InfoService } from '../commons/info/info.service';
import { UserUpdateComponent } from '../components/users/user-update/user-update.component';

describe('PiisConsentService', () => {
  let service: PiisConsentService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PiisConsentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
