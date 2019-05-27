import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {FilterPipeModule} from "ngx-filter-pipe";

import {UsersComponent} from './users.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user.model";
import {Observable} from "rxjs";


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
            ],
            declarations: [UsersComponent],
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
        let mockUsers: User[] = [
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

        let getUsersSpy = spyOn(usersService, 'listUsers').and.returnValue(Observable.of(mockUsers));

        component.ngOnInit();

        expect(getUsersSpy).toHaveBeenCalled();
        expect(component.users).toEqual(mockUsers);
    });
});
