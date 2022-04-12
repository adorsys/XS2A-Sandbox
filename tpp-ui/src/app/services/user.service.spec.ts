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

import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';
import { UserService } from './user.service';

describe('UserService', () => {
  let httpMock: HttpTestingController;
  let userService: UserService;

  const url = `${environment.tppBackend + '/users'}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [UserService],
    });

    httpMock = TestBed.inject(HttpTestingController);
    userService = TestBed.inject(UserService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', inject([UserService], (service: UserService) => {
    expect(service).toBeTruthy();
  }));

  it('should return expected list of users (HttpClient called once)', () => {
    const mockUsers = [
      {
        accountAccesses: [
          {
            id: 'bNrPhmm3SC0vwm2Tf4KknM',
            iban: 'DE51250400903312345678',
            accessType: 'OWNER',
            scaWeight: 70,
            currency: 'EUR',
            accountId: 'accountId',
          },
          {
            id: 'bNrPhmm3SC0vwm2Tf4KknM',
            iban: 'DE51250400903312345678',
            accessType: 'OWNER',
            scaWeight: 70,
            currency: 'EUR',
            accountId: 'accountId',
          },
        ],
        branch: 'fdf',
        email: 'foo@foo.de',
        id: 'J4tdJUEPQhglZAFgvo9aJc',
        login: 'test',
        pin: '$2a$10$hi7Cd4j9gdZBw7wkbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu',
        scaUserData: [
          {
            id: 'HeJDea8LQE8rdLiJ6eKfhY',
            scaMethod: 'SMTP_OTP',
            methodValue: 'foo@fool.de',
            usesStaticTan: false,
            staticTan: '123456',
            decoupled: false,
            valid: false,
          },
        ],
      },
    ];

    userService.listUsers(0, 25, '').subscribe((resp) => {
      expect(resp.users[0].login).toEqual('test');
      expect(resp.totalElements).toEqual(mockUsers.length);
    });
    const req = httpMock.expectOne(`${url}?page=${0}&size=${25}&queryParam=${''}`);
    expect(req.cancelled).toBeFalsy();
    expect(req.request.responseType).toEqual('json');
    expect(req.request.method).toEqual('GET');
    req.flush({ content: mockUsers, totalElements: mockUsers.length });
  });

  it('should get a User', () => {
    userService.getUser('J4tdJUEPQhglZAFgvo9aJc').subscribe((data: any) => {
      expect(data.pin).toBe('$2a$10$hi7Cd4j9gd/ZBw7w.kbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu');
    });
    const req = httpMock.expectOne(url + '/J4tdJUEPQhglZAFgvo9aJc');
    expect(req.request.method).toBe('GET');
    req.flush({
      pin: '$2a$10$hi7Cd4j9gd/ZBw7w.kbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu',
    });
    httpMock.verify();
  });

  it('should create a User', () => {
    const mockUser: User = {
      accountAccesses: [
        {
          id: 'bNrPhmm3SC0vwm2Tf4KknM',
          iban: 'DE51250400903312345678',
          accessType: 'OWNER',
          scaWeight: 70,
          currency: 'EUR',
          accountId: 'accountId',
        },
        {
          id: 'bNrPhmm3SC0vwm2Tf4KknM',
          iban: 'DE51250400903312345678',
          accessType: 'OWNER',
          scaWeight: 70,
          currency: 'EUR',
          accountId: 'accountId',
        },
      ],
      branch: 'fdf',
      branchLogin: 'branchLogin',
      email: 'foo@foo.de',
      id: 'J4tdJUEPQhglZAFgvo9aJc',
      login: 'test',
      pin: '$2a$10$hi7Cd4j9gdZBw7wkbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu',
      scaUserData: [
        {
          id: 'HeJDea8LQE8rdLiJ6eKfhY',
          scaMethod: 'SMTP_OTP',
          methodValue: 'foo@fool.de',
          usesStaticTan: false,
          staticTan: '123456',
          decoupled: false,
          valid: false,
        },
      ],
    };
    userService.createUser(mockUser).subscribe((data: any) => {
      expect(data.login).toBe('test');
    });
    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('POST');
    req.flush({ login: 'test' });
    httpMock.verify();
  });

  it('should updateUserDetails', () => {
    const mockUser: User = {
      accountAccesses: [
        {
          id: 'bNrPhmm3SC0vwm2Tf4KknM',
          iban: 'DE51250400903312345678',
          accessType: 'OWNER',
          scaWeight: 70,
          currency: 'EUR',
          accountId: 'accountId',
        },
        {
          id: 'bNrPhmm3SC0vwm2Tf4KknM',
          iban: 'DE51250400903312345678',
          accessType: 'OWNER',
          scaWeight: 70,
          currency: 'EUR',
          accountId: 'accountId',
        },
      ],
      branch: 'fdf',
      branchLogin: 'branchLogin',
      email: 'foo@foo.de',
      id: 'J4tdJUEPQhglZAFgvo9aJc',
      login: 'test',
      pin: '$2a$10$hi7Cd4j9gdZBw7wkbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu',
      scaUserData: [
        {
          id: 'HeJDea8LQE8rdLiJ6eKfhY',
          scaMethod: 'SMTP_OTP',
          methodValue: 'foo@fool.de',
          usesStaticTan: false,
          staticTan: '123456',
          decoupled: false,
          valid: false,
        },
      ],
    };
    userService.updateUserDetails(mockUser).subscribe((data: any) => {
      expect(data.login).toBe('test');
    });
    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('PUT');
    req.flush({ login: 'test' });
    httpMock.verify();
  });
});
