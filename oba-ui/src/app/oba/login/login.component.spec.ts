/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { AuthService } from '../../common/services/auth.service';
import { of } from 'rxjs';
import { RoutingPath } from 'src/app/common/models/routing-path.model';
import { AccountDetailsComponent } from '../accounts/account-details/account-details.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          MatSnackBarModule,
          RouterTestingModule.withRoutes([
            { path: RoutingPath.LOGIN, component: LoginComponent },
            { path: RoutingPath.ACCOUNTS, component: AccountDetailsComponent },
          ]),
          InfoModule,
        ],
        declarations: [LoginComponent],
        providers: [AuthService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit', () => {
    const loginSpy = spyOn(authService, 'login').and.returnValue(of(true));
    component.onSubmit();
    expect(loginSpy).toHaveBeenCalled();
  });

  it('should throw error 401', () => {
    const errorSpy = spyOn(authService, 'login').and.returnValue(
      of<any>({ success: false })
    );
    component.onSubmit();
    expect(errorSpy).toHaveBeenCalled();
  });
});
