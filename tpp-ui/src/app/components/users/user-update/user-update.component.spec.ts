import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserUpdateComponent} from './user-update.component';
import {UserService} from "../../../services/user.service";
import {Router} from "@angular/router";
import {InfoModule} from "../../../commons/info/info.module";
import {FormArray, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {of} from "rxjs";
import {InfoService} from "../../../commons/info/info.service";
import {User} from "../../../models/user.model";
import {RouterTestingModule} from "@angular/router/testing";
import {IconModule} from "../../../commons/icon/icon.module";
import {ScaMethods} from "../../../models/scaMethods";

describe('UserUpdateComponent', () => {
  let component: UserUpdateComponent;
  let fixture: ComponentFixture<UserUpdateComponent>;
  let userService: UserService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        InfoModule,
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        IconModule
      ],
      providers: [UserService, InfoService],
      declarations: [UserUpdateComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserUpdateComponent);
    component = fixture.componentInstance;
    userService = TestBed.get(UserService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submitted should false', () => {
    expect(component.submitted).toBeFalsy();
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

  it('SCA validity', () => {
    let errors = {};
    const sca = component.updateUserForm.controls['scaUserData']['controls'][0].controls['methodValue'];
    expect(sca.valid).toBeTruthy();

    // pin field is required
    errors = sca.errors || {};
    expect(errors['required']).toBeFalsy();

    // set pin to something correct
    sca.setValue('');
    errors = sca.errors || {};
    expect(errors['required']).toBeFalsy();
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

  it('validate iniScaData method', () => {
    const formGroup = component.initScaData();
    const data = {
      id: '',
      scaMethod: '',
      methodValue: '',
      usesStaticTan: false
    };
    expect(formGroup.value).toEqual(data);
  });

  it('should load actual user and update its details', () => {
    let mockUser: User =
      {
        id: 'XXXXXX',
        email: 'tes@adorsys.de',
        login: 'bob',
        branch: '34256',
        pin: '12345',
        scaUserData: [],
        accountAccesses: [{
          accessType: 'OWNER',
          iban: 'FR87760700254556545403'
        }]
      } as User;

    let getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));

    component.ngOnInit();
    expect(component.submitted).toBeFalsy();
    expect(component.updateUserForm.valid).toBeFalsy();

    // populate form
    const scaUserData = <FormArray>component.updateUserForm.get('scaUserData');
    component.user = mockUser;
    component.updateUserForm.get('email').setValue('dart.vader@dark-side.com');
    component.updateUserForm.get('login').setValue('dart.vader');
    component.updateUserForm.get('pin').setValue('12345678');
    scaUserData.at(0).get('methodValue').setValue('dart.vader@dark-side.com');
    scaUserData.at(0).get('staticTan').setValue('12345');
    scaUserData.at(0).get('usesStaticTan').setValue(true);
    scaUserData.at(0).get('scaMethod').setValue(ScaMethods.EMAIL);

    // create spies and fake call function
    const sampleResponse = {value: 'sample response'};
    let updateUserDetail = spyOn(userService, 'updateUserDetails').and.callFake(() => of(sampleResponse));
    let navigateSpy = spyOn(router, 'navigate');
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

});
