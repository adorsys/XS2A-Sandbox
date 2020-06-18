import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UserProfileComponent } from './user-profile.component';
import { RouterTestingModule } from '@angular/router/testing';
import { UserTO } from '../../api/models/user-to';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { of } from 'rxjs/internal/observable/of';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let mockObaService: OnlineBankingService;

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

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserProfileComponent],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        OnlineBankingService,
        { provide: OnlineBankingService, useValue: mockObaUserService },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    mockObaService = TestBed.get(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getUserInfo()', () => {
    const accountsSpy = spyOn(mockObaService, 'getCurrentUser').and.returnValue(
      of({ mockUser })
    );
    component.getUserInfo();
    expect(accountsSpy).toHaveBeenCalled();
  });
});
