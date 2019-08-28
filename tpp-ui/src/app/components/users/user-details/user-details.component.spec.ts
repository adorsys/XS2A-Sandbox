import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserDetailsComponent} from './user-details.component';
import {UserService} from "../../../services/user.service";
import {RouterTestingModule} from "@angular/router/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {of} from 'rxjs';
import {User} from "../../../models/user.model";

describe('UserDetailsComponent', () => {
  let component: UserDetailsComponent;
  let fixture: ComponentFixture<UserDetailsComponent>;
  let userService: UserService;
  let router: Router;


    beforeEach(async(() => {
    TestBed.configureTestingModule({
        imports: [
            RouterTestingModule.withRoutes([]),
            ReactiveFormsModule,
            HttpClientTestingModule,
        ],
        declarations: [ UserDetailsComponent ],
        providers: [UserService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsComponent);
    component = fixture.componentInstance;
      router = TestBed.get(Router);
      userService = TestBed.get(UserService);
      fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

    it('should load accounts on NgOnInit', () => {
        let mockUser: User =
            {
                id: 'XXXXXX',
                email: 'tes@adorsys.de',
                login: 'bob',
                branch: '',
                pin: '12345',
                scaUserData: {},
                accountAccesses: {}
            } as User;
        let getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));

        component.ngOnInit();

        expect(getUserSpy).toHaveBeenCalled();
        expect(component.user).toEqual(mockUser);
    });
});
