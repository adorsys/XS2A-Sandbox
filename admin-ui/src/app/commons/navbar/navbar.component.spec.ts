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
import { CustomizeService } from '../../services/customize.service';
import { TppUserService } from '../../services/tpp.user.service';
import { NavbarComponent } from './navbar.component';
import { RouterTestingModule } from '@angular/router/testing';
import { IconModule } from '../icon/icon.module';
import { AuthService } from '../../services/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { of, throwError } from 'rxjs';
describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let tppUserService: TppUserService;
  let router: Router;
  let authService: AuthService;
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'isLoggedIn',
    'logout',
  ]);

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          HttpClientTestingModule,
          ReactiveFormsModule,
          IconModule,
        ],
        providers: [
          TestBed.overrideProvider(AuthService, { useValue: authServiceSpy }),
          CustomizeService,
          TppUserService,
          AuthService,
        ],
        declarations: [NavbarComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    authServiceSpy.isLoggedIn.and.returnValue(true);
    fixture.detectChanges();
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
  });

  it('should call loggedIn', () => {
    expect(component).toBeTruthy();
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });

  it('should logout the tpp user', () => {
    component.onLogout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should throw error when the user is not logged in', () => {
    sessionStorage.setItem('access_token', null);
    component.ngDoCheck();
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });
});
