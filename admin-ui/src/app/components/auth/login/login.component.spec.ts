/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { InfoService } from '../../../commons/info/info.service';
import { MatDialog } from '@angular/material/dialog';
import { ERROR_MESSAGE } from '../../../commons/constant/constant';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let infoService: InfoService;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterTestingModule,
          HttpClientModule,
          MatSnackBarModule,
          BrowserAnimationsModule,
        ],
        providers: [
          AuthService,
          InfoService,
          { provide: MatDialog, useValue: {} },
        ],

        declarations: [LoginComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = fixture.debugElement.injector.get(AuthService);
    infoService = TestBed.inject(InfoService);
    fixture.detectChanges();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loginForm should be invalid when at least one field is empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('login field validity', () => {
    let errors = {};
    const login = component.loginForm.controls['login'];
    expect(login.valid).toBeFalsy();

    errors = login.errors || {};
    expect(errors['required']).toBeTruthy();

    login.setValue('test@test.de');
    errors = login.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('pin field validity', () => {
    let errors = {};
    const pin = component.loginForm.controls['pin'];
    expect(pin.valid).toBeFalsy();

    errors = pin.errors || {};
    expect(errors['required']).toBeTruthy();

    pin.setValue('12345678');
    errors = pin.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('Should set error message', () => {
    component.onSubmit();
    expect(component.errorMessage).toEqual('Please enter your credentials');
  });

  it('should login and go to the next page', () => {
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    const logSpy = spyOn(authService, 'login').and.returnValue(of(true));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(logSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/management']);
  });

  it('should throw a error message', () => {
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    const logSpy = spyOn(authService, 'login').and.returnValue(
      throwError({ success: false })
    );
    component.onSubmit();
    expect(logSpy).toHaveBeenCalled();
  });

  it('should show error when session-error is set', () => {
    const errorMessage = 'Logout because of timeout';
    sessionStorage.setItem(ERROR_MESSAGE, errorMessage);
    const feedBackSpy = spyOn(infoService, 'openFeedback');
    component.ngOnInit();
    expect(feedBackSpy).toHaveBeenCalledWith(errorMessage, {
      severity: 'error',
    });
  });

  it('should show no error-message', () => {
    sessionStorage.setItem(ERROR_MESSAGE, null);
    const feedBackSpy = spyOn(infoService, 'openFeedback');
    component.ngOnInit();
    expect(feedBackSpy).toHaveBeenCalledTimes(0);
  });
});
