import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFundsConfirmationDetailsComponent } from './user-funds-confirmation-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { PiisConsentService } from '../../../services/piis-consent.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { PiisConsent } from '../../../models/user.model';
import { EMPTY, Observable } from 'rxjs';

describe('UserFundsConfirmationDetailsComponent', () => {
  let component: UserFundsConfirmationDetailsComponent;
  let fixture: ComponentFixture<UserFundsConfirmationDetailsComponent>;
  let router: Router;
  let activate: ActivatedRoute;

  let mockPiisConsentService = {
    getPiisConsent(consentId: string, userLogin: string): Observable<PiisConsent> {
      return EMPTY;
    },
    createPiisConsent(piisConsent: PiisConsent, userLogin: string, password: string): Observable<any> {
      return EMPTY;
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([]), HttpClientTestingModule],
      providers: [{ provide: PiisConsentService, useValue: mockPiisConsentService }],
      declarations: [UserFundsConfirmationDetailsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserFundsConfirmationDetailsComponent);
    router = TestBed.inject(Router);
    activate = TestBed.inject(ActivatedRoute);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
