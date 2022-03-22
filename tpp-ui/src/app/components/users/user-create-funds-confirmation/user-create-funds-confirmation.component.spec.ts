import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserCreateFundsConfirmationComponent } from './user-create-funds-confirmation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { PiisConsentService } from '../../../services/piis-consent.service';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { EMPTY, Observable, of } from 'rxjs';
import { PiisConsent, User } from '../../../models/user.model';
import { UserService } from '../../../services/user.service';
import { CurrencyService } from 'src/app/services/currency.service';
import { SpinnerVisibilityService } from 'ng-http-loader';
import { InfoService } from 'src/app/commons/info/info.service';
import { InfoOptions } from 'src/app/commons/info/info-options';

describe('UserCreateFundsConfirmationConsentComponent', () => {
  let component: UserCreateFundsConfirmationComponent;
  let fixture: ComponentFixture<UserCreateFundsConfirmationComponent>;
  let piisConsentService: PiisConsentService;
  let userService: UserService;
  let spinnerVisibilityService: SpinnerVisibilityService;
  let router: Router;
  let de: DebugElement;

  let mockInfoService = {
    openFeedback(message: string, options?: Partial<InfoOptions>) {},
  };

  let mockPiisConsentService = {
    createPiisConsent(piisConsent: PiisConsent, userLogin: string, password: string): Observable<any> {
      return EMPTY;
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientModule],
      providers: [
        UserService,
        CurrencyService,
        SpinnerVisibilityService,
        { provide: PiisConsentService, useValue: mockPiisConsentService },
      ],
      declarations: [UserCreateFundsConfirmationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserCreateFundsConfirmationComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    userService = TestBed.inject(UserService);
    spinnerVisibilityService = TestBed.inject(SpinnerVisibilityService);
    piisConsentService = TestBed.inject(PiisConsentService);

    let mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '',
      pin: '12345',
      scaUserData: {},
      accountAccesses: {},
      branchLogin: 'branchLogin',
    } as User;
    let getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));

    component.getUserDetails();
    expect(getUserSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
    component.ngOnInit();
  });

  it('user should be equal', () => {
    let mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '',
      pin: '12345',
      scaUserData: {},
      accountAccesses: {},
      branchLogin: 'branchLogin',
    } as User;

    expect(component.user).toEqual(mockUser);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('password field validity', () => {
    de = fixture.debugElement.query(By.css('form'));
    fixture.detectChanges();
    let errors = {};
    const password = component.createFundsFormGroup.controls['password'];
    expect(password.valid).toBeFalsy();
    errors = password.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('iban field validity', () => {
    let errors = {};
    const iban = component.createFundsFormGroup.controls['iban'];
    expect(iban.valid).toBeFalsy();
    errors = iban.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('tppAuthorisationNumber field validity', () => {
    let errors = {};
    const tppAuthorisationNumber = component.createFundsFormGroup.controls['tppAuthorisationNumber'];
    expect(tppAuthorisationNumber.valid).toBeFalsy();
    errors = tppAuthorisationNumber.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('validUntil field validity', () => {
    let errors = {};
    const validUntil = component.createFundsFormGroup.controls['validUntil'];
    expect(validUntil.valid).toBeFalsy();
    errors = validUntil.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('button should be disabled', () => {
    let object = fixture.debugElement.nativeElement;
    let button = object.querySelector('button');
    fixture.detectChanges();
    expect(button.disabled).toBeFalse();
  });

  it('create confirmation consent should be created', () => {
    de = fixture.debugElement.query(By.css('form'));
    fixture.detectChanges();
    let button = fixture.nativeElement.querySelector('button');
    component.createFundsFormGroup.get('password').setValue('foo');
    component.createFundsFormGroup.get('iban').setValue('234234234');
    component.createFundsFormGroup.get('validUntil').setValue('2022-02-25');
    component.createFundsFormGroup.get('tppAuthorisationNumber').setValue('234523453');
    fixture.detectChanges();
    expect(button.disabled).toBeFalsy();
    console.log(button);
    /*const logCreatePiisConsentSpy = spyOn(piisConsentService, 'createPiisConsent');
    component.onSubmit();
    expect(logCreatePiisConsentSpy).toHaveBeenCalled();*/
  });
});
