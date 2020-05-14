import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { IconModule } from '../../../commons/icon/icon.module';
import { InfoModule } from '../../../commons/info/info.module';
import { InfoService } from '../../../commons/info/info.service';
import { UserService } from '../../../services/user.service';
import { UserCreateComponent } from './user-create.component';
import { ScaMethods } from '../../../models/scaMethods';
import { of, throwError } from 'rxjs';

describe('UserCreateComponent', () => {
  let component: UserCreateComponent;
  let fixture: ComponentFixture<UserCreateComponent>;
  let userService: UserService;
  let infoService: InfoService;
  let router: Router;
  let de: DebugElement;
  let el: HTMLElement;

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
      declarations: [UserCreateComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserCreateComponent);
    component = fixture.componentInstance;
    userService = TestBed.get(UserService);
    infoService = TestBed.get(InfoService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submitted should false', () => {
    expect(component.submitted).toBeFalsy();
  });

  it('userForm should be invalid when at least one field is empty', () => {
    expect(component.userForm.valid).toBeFalsy();
  });

  it('email field validity', () => {
    let errors = {};
    const email = component.userForm.controls['email'];
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
    const login = component.userForm.controls['login'];
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
    const pin = component.userForm.controls['pin'];
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
    const sca =
      component.userForm.controls['scaUserData']['controls'][0].controls[
        'methodValue'
      ];
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
    expect(component.userForm.valid).toBeFalsy();
  });

  it('validate setupUserFormControl method', () => {
    component.setupUserFormControl();
    expect(component.userForm).toBeDefined();
  });

  it('validate formControl method', () => {
    expect(component.formControl).toEqual(component.userForm.controls);
  });

  it('validate addScaData method', () => {
    const length = (<FormArray>component.userForm.controls['scaUserData'])
      .length;
    component.addScaDataItem();
    const newLength = (<FormArray>component.userForm.controls['scaUserData'])
      .length;
    expect(newLength).toEqual(length + 1);
  });

  it('validate removeScaDataItem method', () => {
    component.removeScaDataItem(0);
    const length = (<FormArray>component.userForm.controls['scaUserData'])
      .length;
    expect(length).toEqual(0);
  });

  it('validate iniScaData method', () => {
    const formGroup = component.initScaData();
    const data = {
      scaMethod: '',
      methodValue: '',
      usesStaticTan: false,
    };
    expect(formGroup.value).toEqual(data);
  });

  it('should call user service when form is valid and submitted', () => {
    component.ngOnInit();
    expect(component.submitted).toBeFalsy();
    expect(component.userForm.valid).toBeFalsy();

    // populate form
    component.userForm.controls['email'].setValue('dart.vader@dark-side.com');
    component.userForm.controls['login'].setValue('dart.vader');
    component.userForm.controls['pin'].setValue('12345678');
    component.userForm.controls['scaUserData']['controls'][0].controls[
      'methodValue'
    ].setValue('dart.vader@dark-side.com');
    component.userForm.controls['scaUserData']['controls'][0].controls[
      'staticTan'
    ].setValue('12345');
    component.userForm.controls['scaUserData']['controls'][0].controls[
      'usesStaticTan'
    ].setValue(true);
    component.userForm.controls['scaUserData']['controls'][0].controls[
      'scaMethod'
    ].setValue(ScaMethods.EMAIL);

    // create spies and fake call function
    const sampleResponse = { value: 'sample response' };
    let createUserSpy = spyOn(userService, 'createUser').and.callFake(() =>
      of(sampleResponse)
    );
    let navigateSpy = spyOn(router, 'navigateByUrl');
    component.onSubmit();
    expect(component.submitted).toBeTruthy();
    expect(component.userForm.valid).toBeTruthy();
    expect(createUserSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith('/users/all');
  });

  it('should back to users', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(['/users/all']);
  });
});
