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

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FilterPipeModule } from 'ngx-filter-pipe';
import { of } from 'rxjs';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { UsersComponent } from './users.component';
import { PaginationContainerComponent } from '../../commons/pagination-container/pagination-container.component';
import { PaginationConfigModel } from '../../models/pagination-config.model';
import { InfoService } from '../../commons/info/info.service';
import { OverlayModule } from '@angular/cdk/overlay';
import { ADMIN_KEY } from '../../commons/constant/constant';

describe('UsersComponent', () => {
  let component: UsersComponent;
  let fixture: ComponentFixture<UsersComponent>;
  let usersService: UserService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          FormsModule,
          FilterPipeModule,
          RouterTestingModule,
          HttpClientTestingModule,
          NgbPaginationModule,
          NgbPaginationModule,
          OverlayModule,
        ],
        declarations: [UsersComponent, PaginationContainerComponent],
        providers: [UserService, InfoService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    usersService = TestBed.inject(UserService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on NgOnInit', () => {
    sessionStorage.setItem(ADMIN_KEY, 'false');
    component.ngOnInit();
    const mockUsers: User[] = [
      {
        id: 'USERID',
        email: 'user@gmail.com',
        login: 'user',
        branch: 'branch',
        pin: '123345',
        scaUserData: [],
        accountAccesses: [],
        branchLogin: 'branchLogin',
      },
    ];
    const getUsersSpy = spyOn(usersService, 'listUsers').and.returnValue(of({ users: mockUsers, totalElements: mockUsers.length }));

    component.ngOnInit();

    expect(getUsersSpy).toHaveBeenCalled();
    expect(component.users).toEqual(mockUsers);
  });

  it('should load users', () => {
    component.admin = 'false';
    const mockUsers: User[] = [
      {
        id: 'USERID',
        email: 'user@gmail.com',
        login: 'user',
        branch: 'branch',
        pin: '123345',
        scaUserData: [],
        accountAccesses: [],
        branchLogin: 'branchLogin',
      },
    ];

    const getUsersSpy = spyOn(usersService, 'listUsers').and.returnValue(of({ users: mockUsers, totalElements: mockUsers.length }));

    component.listUsers(5, 10, {});

    expect(getUsersSpy).toHaveBeenCalled();
    expect(component.users).toEqual(mockUsers);
    expect(component.config.totalItems).toEqual(mockUsers.length);
  });

  it('should pageChange', () => {
    const mockPageConfig = {
      pageNumber: 10,
      pageSize: 5,
    };
    component.searchForm.setValue({
      userLogin: 'foo',
      tppId: 'foo',
      tppLogin: 'foo',
      country: 'foo',
      blocked: 'foo',
      itemsPerPage: 15,
    });
    const listUsersSpy = spyOn(component, 'listUsers');
    component.pageChange(mockPageConfig);
    expect(listUsersSpy).toHaveBeenCalledWith(
      10,
      5,
      Object({
        userLogin: 'foo',
        tppId: 'foo',
        tppLogin: 'foo',
        country: 'foo',
        blocked: 'foo',
      })
    );
  });

  it('should change the page size', () => {
    const paginationConfigModel: PaginationConfigModel = {
      itemsPerPage: 0,
      currentPageNumber: 0,
      totalItems: 0,
    };
    component.config = paginationConfigModel;
    component.changePageSize(10);
    expect(component.config.itemsPerPage).toEqual(10);
  });
});
