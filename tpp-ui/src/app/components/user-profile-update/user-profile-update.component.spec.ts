import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {TppUserService} from '../../services/tpp.user.service';
import {AuthService} from '../../services/auth.service';
import {TppService} from "../../services/tpp.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import { Router } from '@angular/router';
import { DebugElement } from '@angular/core';
import {User} from '../../models/user.model';
import { UserProfileUpdateComponent } from './user-profile-update.component';
import {of} from 'rxjs';

describe('UserProfileUpdateComponent', () => {
  let component: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;
  let userInfoService: TppUserService;
  let router: Router;
  let de: DebugElement;
  let el: HTMLElement;

    const mockUser: User = {
        id: 'id',
        email: 'email',
        login: 'login',
        branch: 'branch',
        pin: 'pin',
        scaUserData: [],
        accountAccesses: []
    }

    const mockAuthUserService = {
        isLoggedIn: () => {
            return true;
        }
    };

    const mockinfoService = {
        getUserInfo: () => of(mockUser),
    };

    beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [TppUserService, AuthService, TppService, NgbModal,
          {provide: AuthService, useValue: mockAuthUserService},
          {provide: TppUserService, useValue: mockinfoService}],
      declarations: [ UserProfileUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    component = fixture.componentInstance;
    userInfoService = TestBed.get(TppUserService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get UserDetails component', () => {
      component.getUserDetails();
      expect(component.user).toEqual(mockUser)
  });

  it('validate onSubmit method', () => {
      component.onSubmit();
      expect(component.submitted).toEqual(true);
      expect(component.userForm.valid).toBeFalsy();
  });

  it('validate setupUserFormControl method', () => {
      component.setupEditUserFormControl();
      expect(component.userForm).toBeDefined();
  });

  it('validate formControl method', () => {
      expect(component.formControl).toEqual(component.userForm.controls);
  });
});
