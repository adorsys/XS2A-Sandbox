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
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UserProfileComponent } from './user-profile.component';
import { TppUserService } from '../../services/tpp.user.service';
import { AuthService } from '../../services/auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../models/user.model';
import { of } from 'rxjs';
import { InfoService } from '../../commons/info/info.service';
import { OverlayModule } from '@angular/cdk/overlay';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Store } from '@ngxs/store';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {  NO_ERRORS_SCHEMA } from '@angular/core';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;

  const mockUser: User = {
    id: 'id',
    email: 'email',
    login: 'login',
    branch: 'branch',
    pin: 'pin',
    scaUserData: [],
    accountAccesses: [],
    branchLogin: 'branchLogin',
    userRoles: ['SYSTEM'],
  };

  const mockTppUserService = {
    currentTppUser: of(mockUser),
    getUserInfo: () => of(mockUser),
  };

  const mockAuthUserService = {
    isLoggedIn: () => {
      return true;
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        OverlayModule,
        RouterTestingModule,
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule,
      ],
      providers: [
        NgbModal,
        AuthService,
        TppUserService,
        InfoService,
        { provide: Store, useValue: {} },
        { provide: BsModalService, useValue: {} },
        { provide: AuthService, useValue: mockAuthUserService },
        { provide: TppUserService, useValue: mockTppUserService },
      ],
      declarations: [UserProfileComponent],
      schemas: [
        NO_ERRORS_SCHEMA
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init component', () => {
    component.ngOnInit();
    expect(component.tppUser).toEqual(mockUser);
  });
});
