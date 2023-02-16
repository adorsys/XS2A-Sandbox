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

import { ResetPasswordComponent } from './reset-password.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InfoService } from '../../../commons/info/info.service';
import { OverlayModule } from '@angular/cdk/overlay';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule, BrowserAnimationsModule, OverlayModule],
        declarations: [ResetPasswordComponent],
        providers: [InfoService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.ngOnInit();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loginForm should be invalid when at least one field is empty', () => {
    expect(component.resetPasswordForm.valid).toBeFalsy();
  });

  it('login field validity', () => {
    let errors = {};
    const login = component.resetPasswordForm.controls['login'];
    expect(login.valid).toBeFalsy();

    // login field is required
    errors = login.errors || {};
    expect(errors['required']).toBeTruthy();

    // set login to something correct
    login.setValue('foo');
    errors = login.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('Should set error message', () => {
    component.onSubmit();
    expect(component.errorMessage).toEqual('Please enter valid login');
  });

  // TODO write unite tests https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/704
});
