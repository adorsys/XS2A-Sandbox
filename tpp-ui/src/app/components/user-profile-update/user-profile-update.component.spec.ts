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

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { TppUserService } from '../../services/tpp.user.service';
import { AuthService } from '../../services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user.model';
import { UserProfileUpdateComponent } from './user-profile-update.component';
import { of } from 'rxjs';
import { TppManagementService } from '../../services/tpp-management.service';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../commons/info/info.module';
import { InfoService } from '../../commons/info/info.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('UserProfileUpdateComponent', () => {
  let component: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;
  let userInfoService: TppUserService;

  const mockRoute = {
    snapshot: { params: of({ id: '12345' }) },
    params: of({ id: '12345' }),
    queryParams: of({}),
  };

  const mockUser: User = {
    id: 'id',
    email: 'email',
    login: 'login',
    branch: 'branch',
    pin: 'pin',
    userRoles: [],
    scaUserData: [],
    accountAccesses: [],
    branchLogin: 'branchLogin',
  };

  const mockTppUserService = {
    currentTppUser: of(mockUser),
    getUserInfo: () => of(mockUser),
    updateUserInfo: () => of({}),
  };

  const mockRouter = {
    navigate: (url: string) => {
      console.log('mocknavigation', url);
    },
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule.withRoutes([]), ReactiveFormsModule, InfoModule, HttpClientTestingModule, BrowserAnimationsModule],
        providers: [
          {
            provide: InfoService,
            TppUserService,
            AuthService,
            TppManagementService,
            NgbModal,
          },
          { provide: ActivatedRoute, useValue: mockRoute },
          { provide: TppUserService, useValue: mockTppUserService },
          { provide: Router, useValue: mockRouter },
        ],
        declarations: [UserProfileUpdateComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    component = fixture.componentInstance;
    userInfoService = TestBed.inject(TppUserService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get UserDetails component', () => {
    sessionStorage.setItem('access_token', 'Real session token');
    component.getUserDetails();
    expect(component.user).toEqual(mockUser);
  });

  it('validate onSubmit method', () => {
    component.onSubmit();
    expect(component.userForm.valid).toBeFalsy();
  });

  it('validate setupUserFormControl method', () => {
    component.setupEditUserFormControl();
    expect(component.userForm).toBeDefined();
  });

  it('validate formControl method', () => {
    expect(component.formControl).toEqual(component.userForm.controls);
  });

  it('should load the update users info', () => {
    const infoSpy = spyOn(userInfoService, 'updateUserInfo').and.returnValue(of({ mockUser }));
    component.user = mockUser;
    component.userForm.get('email').setValue('dart.vader@gmail.com');
    component.userForm.get('username').setValue('dart.vader');
    expect(component.userForm.valid).toBeTruthy();
    component.admin = 'false';
    component.onSubmit();
    expect(infoSpy).toHaveBeenCalled();
  });
});
