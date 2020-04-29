import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { UserUpdateComponent } from './user-update.component';
import { UserService } from '../../../services/user.service';
import { Router, ActivatedRoute } from '@angular/router';
import { InfoModule } from '../../../commons/info/info.module';
import { FormArray, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { InfoService } from '../../../commons/info/info.service';
import { User } from '../../../models/user.model';
import { RouterTestingModule } from '@angular/router/testing';
import { IconModule } from '../../../commons/icon/icon.module';
import { ScaMethods } from '../../../models/scaMethods';

describe('UserUpdateComponent', () => {
  let component: UserUpdateComponent;
  let fixture: ComponentFixture<UserUpdateComponent>;
  let userService: UserService;
  let infoService: InfoService;
  let activate: ActivatedRoute;
  let router: Router;
  let de: DebugElement;
  let el: HTMLElement;

  const mockUser: User = {
    id: 'id',
    email: 'email',
    login: 'login',
    branch: 'branch',
    pin: 'pin',
    scaUserData: [],
    accountAccesses: [],
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        InfoModule,
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        IconModule,
      ],
      providers: [UserService, InfoService],
      declarations: [UserUpdateComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserUpdateComponent);
    component = fixture.componentInstance;
    userService = TestBed.get(UserService);
    infoService = TestBed.get(InfoService);
    router = TestBed.get(Router);
    activate = TestBed.get(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submitted should false', () => {
    expect(component.submitted).toBeFalsy();
  });

  it('updateUserForm should be invalid when at least one field is empty', () => {
    expect(component.updateUserForm.valid).toBeFalsy();
  });

  it('email field validity', () => {
    let errors = {};
    const email = component.updateUserForm.controls['email'];
    expect(email.valid).toBeFalsy();

    // email field is required
    errors = email.errors || {};
    expect(errors['required']).toBeTruthy();

    // set email to something incorrect
    email.setValue('testtests.de');
    errors = email.errors || {};
    expect(errors['email']).toBeTruthy();

    // set email to something correct
    email.setValue('test@test.de');
    errors = email.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('login field validity', () => {
    let errors = {};
    const login = component.updateUserForm.controls['login'];
    expect(login.valid).toBeFalsy();

    // login field is required
    errors = login.errors || {};
    expect(errors['required']).toBeTruthy();

    // set login to something correct
    login.setValue('test@test.de');
    errors = login.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('pin field validity', () => {
    let errors = {};
    const pin = component.updateUserForm.controls['pin'];
    expect(pin.valid).toBeFalsy();

    // pin field is required
    errors = pin.errors || {};
    expect(errors['required']).toBeTruthy();

    // pin should have at least 5 characters
    pin.setValue('1234');
    errors = pin.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(errors['minlength']).toBeTruthy();

    // set pin to something correct
    pin.setValue('12345678');
    errors = pin.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('validate addScaData method', () => {
    const length = (<FormArray>component.updateUserForm.controls['scaUserData'])
      .length;
    component.addScaDataItem();
    const newLength = (<FormArray>(
      component.updateUserForm.controls['scaUserData']
    )).length;
    expect(newLength).toEqual(length + 1);
  });

  it('validate removeScaDataItem method', () => {
    component.removeScaDataItem(0);
    const length = (<FormArray>component.updateUserForm.controls['scaUserData'])
      .length;
    expect(length).toEqual(0);
  });

  it('validate onSubmit method', () => {
    component.onSubmit();
    expect(component.submitted).toEqual(true);
    expect(component.updateUserForm.valid).toBeFalsy();
  });

  it('validate setupUserFormControl method', () => {
    component.setupUserFormControl();
    expect(component.updateUserForm).toBeDefined();
  });

  it('validate formControl method', () => {
    expect(component.formControl).toEqual(component.updateUserForm.controls);
  });

  it('should enable staticTan when useStaticTan is true', () => {
    const scaData = component.initScaData();
    scaData.get('usesStaticTan').setValue(true);
    const enabled = scaData.get('staticTan').enabled;
    expect(enabled).toBe(true);
  });

  it('should enable staticTan when useStaticTan is true', () => {
    const expectedValue = '';
    const scaData = component.initScaData();
    scaData.get('usesStaticTan').setValue(true);
    scaData.get('usesStaticTan').setValue(false);
    const disabled = scaData.get('staticTan').disabled;
    const value = scaData.get('staticTan').value;
    expect(disabled).toBe(true);
    expect(value).toBe(expectedValue);
  });

  it('should init scaUserDataFrom when user has saved scaUserData', () => {
    component.setupUserFormControl();
    let mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '34256',
      pin: '12345',
      scaUserData: [
        {
          id: '01',
          scaMethod: '',
          methodValue: '',
          usesStaticTan: false,
          decoupled: false,
          valid: false,
        },
        {
          id: '02',
          scaMethod: '',
          methodValue: '',
          usesStaticTan: false,
          decoupled: false,
          valid: false,
        },
      ],
      accountAccesses: [],
    } as User;
    spyOn(userService, 'getUser').and.returnValue(of(mockUser));
    component.getUserDetails();
    const scaUserDataGroups = <FormArray>(
      component.updateUserForm.get('scaUserData')
    );
    const length = scaUserDataGroups.length;
    expect(length).toBe(2);
  });

  it('should load actual user and update its details', () => {
    let mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '34256',
      pin: '12345',
      scaUserData: [],
      accountAccesses: [
        {
          accessType: 'OWNER',
          iban: 'FR87760700254556545403',
        },
      ],
    } as User;

    let getUserSpy = spyOn(userService, 'getUser').and.returnValue(
      of(mockUser)
    );

    component.ngOnInit();
    expect(component.submitted).toBeFalsy();
    expect(component.updateUserForm.valid).toBeTruthy();

    const scaUserData = <FormArray>component.updateUserForm.get('scaUserData');
    component.user = mockUser;
    component.updateUserForm.get('email').setValue('dart.vader@dark-side.com');
    component.updateUserForm.get('login').setValue('dart.vader');
    component.updateUserForm.get('pin').setValue('12345678');

    const sampleResponse = { value: 'sample response' };
    const updateUserDetail = spyOn(
      userService,
      'updateUserDetails'
    ).and.callFake(() => of(sampleResponse));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    const submittedUser = updateUserDetail.calls.argsFor(0)[0] as User;
    expect(submittedUser.email).toBe('dart.vader@dark-side.com');
    expect(submittedUser.accountAccesses).toEqual(mockUser.accountAccesses);
    expect(component.submitted).toBeTruthy();
    expect(component.updateUserForm.valid).toBeTruthy();
    expect(getUserSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
    expect(navigateSpy).toHaveBeenCalledWith(['/users/all']);
  });

  it('should defined getMethodsValues', () => {
    component.getMethodsValues();
    expect(component.methods).toEqual([
      'EMAIL',
      'MOBILE',
      'CHIP_OTP',
      'PHOTO_OTP',
      'PUSH_OTP',
      'SMS_OTP',
      'APP_OTP',
    ]);
  });

  it('should back to users', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(['/users/all']);
  });
});
