import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UserProfileComponent } from './user-profile.component';
import { TppUserService } from '../../services/tpp.user.service';
import { AuthService } from '../../services/auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { of } from 'rxjs';
import { TppManagementService } from '../../services/tpp-management.service';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let tppUserService: TppUserService;
  let tppService: TppManagementService;
  let authService: AuthService;
  let router: Router;

  const mockUser: User = {
    id: 'id',
    email: 'email',
    login: 'login',
    branch: 'branch',
    pin: 'pin',
    scaUserData: [],
    accountAccesses: [],
  };

  const mockTppUserService = {
    getUserInfo: () => of(mockUser),
  };

  const mockAuthUserService = {
    isLoggedIn: () => {
      return true;
    },
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        RouterTestingModule.withRoutes([]),
      ],
      providers: [
        TppManagementService,
        NgbModal,
        AuthService,
        TppUserService,
        { provide: AuthService, useValue: mockAuthUserService },
        { provide: TppUserService, useValue: mockTppUserService },
      ],
      declarations: [UserProfileComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    tppUserService = TestBed.get(TppUserService);
    tppService = TestBed.get(TppManagementService);
    authService = TestBed.get(AuthService);
    router = TestBed.get(Router);
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
