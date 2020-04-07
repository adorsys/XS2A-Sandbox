import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FilterPipeModule } from 'ngx-filter-pipe';
import { of } from 'rxjs';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { UsersComponent } from './users.component';
import {PaginationContainerComponent} from "../../commons/pagination-container/pagination-container.component";
import {PageConfig, PaginationConfigModel} from "../../models/pagination-config.model";


describe('UsersComponent', () => {
    let component: UsersComponent;
    let fixture: ComponentFixture<UsersComponent>;
    let usersService: UserService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                FormsModule,
                FilterPipeModule,
                RouterTestingModule,
                HttpClientTestingModule,
                NgbPaginationModule,
                NgbPaginationModule,
            ],
            declarations: [UsersComponent, PaginationContainerComponent],
            providers: [UserService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(UsersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        usersService = TestBed.get(UserService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load users on NgOnInit', () => {
        component.ngOnInit();
        const mockUsers: User[] = [
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: 'user',
                branch: 'branch',
                pin: '123345',
                scaUserData: [],
                accountAccesses: []
            }
        ];

        const getUsersSpy = spyOn(usersService, 'listUsers').and.returnValue(of({users: mockUsers, totalElements: mockUsers.length}));

        component.ngOnInit();

        expect(getUsersSpy).toHaveBeenCalled();
        expect(component.users).toEqual(mockUsers);
    });

    it('should load users',  () => {
        const mockUsers: User[] = [
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: 'user',
                branch: 'branch',
                pin: '123345',
                scaUserData: [],
                accountAccesses: []
            }
        ];
        const getUsersSpy = spyOn(usersService, 'listUsers').and.returnValue(of({users: mockUsers, totalElements: mockUsers.length}));

        component.listUsers(5,10, 'string');

        expect(getUsersSpy).toHaveBeenCalled();
        expect(component.users).toEqual(mockUsers);
        expect(component.config.totalItems).toEqual(mockUsers.length);
    });

    it('should pageChange', () => {
        const mockPageConfig = {
            pageNumber: 10,
            pageSize: 5
        }
        component.searchForm.setValue({
                                    query: 'foo',
                                    itemsPerPage: 15});
        const listUsersSpy = spyOn(component, 'listUsers');
        component.pageChange(mockPageConfig);
        expect(listUsersSpy).toHaveBeenCalledWith(10, 5, 'foo');
    });

    it('should change the page size', () => {
        const paginationConfigModel: PaginationConfigModel = {
            itemsPerPage: 0,
            currentPageNumber: 0,
            totalItems: 0
        }
        component.config = paginationConfigModel;
        component.changePageSize(10);
        expect(component.config.itemsPerPage).toEqual(10);
    });

});
