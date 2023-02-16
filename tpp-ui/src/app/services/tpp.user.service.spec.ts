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

import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TppUserService } from './tpp.user.service';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';

describe('TppUserService', () => {
  let httpMock: HttpTestingController;
  let tppUserService: TppUserService;
  const url = `${environment.tppBackend}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [TppUserService],
    });
    tppUserService = TestBed.inject(TppUserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(tppUserService).toBeTruthy();
  });

  it('should load User info', () => {
    tppUserService.getUserInfo().subscribe();
    const req = httpMock.expectOne(url + '/users/me');
    expect(req.request.method).toBe('GET');
    req.flush({ pin: '12345' });
    httpMock.verify();
  });

  it('should load User info', () => {
    tppUserService.getUserInfo().subscribe();
    const req = httpMock.expectOne(url + '/users/me');
    expect(req.request.method).toBe('GET');
    req.flush({ pin: '12345' });
    httpMock.verify();
  });

  it('should update User Info', () => {
    const mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branchLogin: 'branchLogin',
      branch: '34256',
      pin: '12345',
      scaUserData: [],
      accountAccesses: [
        {
          accessType: 'OWNER',
          iban: 'FR87760700254556545403',
        },
      ],
    } as User;
    tppUserService.updateUserInfo(mockUser).subscribe((data: User) => {
      expect(data.login).toBe('bob');
    });
    const req = httpMock.expectOne(url + '/users');
    expect(req.request.method).toBe('PUT');
    req.flush({ login: 'bob' });
    httpMock.verify();
  });
});
