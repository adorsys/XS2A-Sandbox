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
import { UserProfileComponent } from './user-profile.component';
import { RouterTestingModule } from '@angular/router/testing';
import { UserTO } from '../../api/models/user-to';
import { of } from 'rxjs/internal/observable/of';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CurrentUserService } from '../../common/services/current-user.service';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let mockObaService: CurrentUserService;

  const mockUser: UserTO = {
    accountAccesses: [],
    branch: 'branch',
    email: 'email',
    id: 'id',
    login: 'login',
    pin: 'pin',
    scaUserData: [],
    userRoles: [],
  };

  const mockObaUserService = {
    getCurrentUser: () => of(mockUser),
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [UserProfileComponent],
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [
          CurrentUserService,
          { provide: CurrentUserService, useValue: mockObaUserService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    mockObaService = TestBed.inject(CurrentUserService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getUserInfo()', () => {
    const accountsSpy = spyOn(mockObaService, 'getCurrentUser').and.returnValue(
      of<any>({ mockUser })
    );
    component.getUserInfo();
    expect(accountsSpy).toHaveBeenCalled();
  });
});
