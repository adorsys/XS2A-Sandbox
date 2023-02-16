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
/* eslint-disable @typescript-eslint/no-empty-function */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { InfoService } from '../../common/info/info.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../common/services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBarModule } from '@angular/material/snack-bar';

const mockRouter = {
  navigate: () => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345', redirectId: 'asdfa', encryptedConsentId: '23948' }),
  queryParams: of({ redirectId: 'asdfa', encryptedConsentId: '23948' }),
};
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterTestingModule,
          HttpClientModule,
          MatSnackBarModule,
        ],
        providers: [
          AuthService,
          InfoService,
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],

        declarations: [LoginComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);

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
    spyOn(router, 'navigate');
    const authoriseSpy = spyOn(component, 'aisAuthorise').and.callFake(logSpy);
    component.onSubmit();

    expect(authoriseSpy).toHaveBeenCalledWith(
      Object({
        pin: '12345',
        login: 'foo',
        encryptedConsentId: '23948',
        authorisationId: 'asdfa',
      })
    );
    expect(logSpy).toHaveBeenCalled();
  });
});
