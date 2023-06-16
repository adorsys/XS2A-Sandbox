/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConfirmNewPasswordComponent } from './confirm-new-password.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { AuthService } from '../../../services/auth.service';

describe('ConfirmNewPasswordComponent', () => {
  let component: ConfirmNewPasswordComponent;
  let fixture: ComponentFixture<ConfirmNewPasswordComponent>;
  let authService: AuthService;
  let authServiceSpy;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule, BrowserAnimationsModule],
      providers: [AuthService],
      declarations: [ConfirmNewPasswordComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmNewPasswordComponent);
    component = fixture.componentInstance;
    authService = fixture.debugElement.injector.get(AuthService);

    de = fixture.debugElement.query(By.css('form'));
    el = de.nativeElement;

    fixture.detectChanges();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call changePassword on the service', () => {
    authServiceSpy = spyOn(authService, 'changePassword').and.callThrough();

    const form = component.confirmNewPasswordForm;
    form.controls['newPassword'].setValue('12345');
    form.controls['code'].setValue('12345678');

    el = fixture.debugElement.query(By.css('button')).nativeElement;
    el.click();

    expect(authServiceSpy).toHaveBeenCalledWith({
      newPassword: '12345',
      code: '12345678',
    });
    expect(authServiceSpy).toHaveBeenCalled();
  });

  it('confirmNewPasswordForm should be invalid when at least one field is empty', () => {
    expect(component.confirmNewPasswordForm.valid).toBeFalsy();
  });

  it('New password field validity', () => {
    let errors = {};
    const confirmNewPassword = component.confirmNewPasswordForm.controls['newPassword'];
    expect(confirmNewPassword.valid).toBeFalsy();

    // confirmNewPassword field is required
    errors = confirmNewPassword.errors || {};
    expect(errors['required']).toBeTruthy();

    // set confirmNewPassword to something correct
    confirmNewPassword.setValue('123345');
    errors = confirmNewPassword.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('code field validity', () => {
    let errors = {};
    const code = component.confirmNewPasswordForm.controls['code'];
    expect(code.valid).toBeFalsy();

    // code field is required
    errors = code.errors || {};
    expect(errors['required']).toBeTruthy();

    // set code to something correct
    code.setValue('12345678');
    errors = code.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('Should set error message', () => {
    component.onSubmit();
    expect(component.errorMessage).toEqual('Please enter your credentials');
  });

  it('Set error message by button click and invalid form', () => {
    el = fixture.debugElement.query(By.css('button')).nativeElement;
    el.click();
    expect(component.errorMessage).toEqual('Please enter your credentials');
  });
});
