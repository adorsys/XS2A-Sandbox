import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AccountService} from '../../../services/account.service';
import {UserDetailsComponent} from './user-details.component';
import {UserService} from "../../../services/user.service";
import {RouterTestingModule} from "@angular/router/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Router, ActivatedRoute} from "@angular/router";
import {of, throwError} from 'rxjs';
import {User} from "../../../models/user.model";
import {EmailVerificationService} from "../../../services/email-verification.service";
import {InfoService} from "../../../commons/info/info.service";
import {InfoModule} from "../../../commons/info/info.module";
import {Account} from '../../../models/account.model';
import { AccountStatus, AccountType, UsageType } from '../../../models/account.model';

describe('UserDetailsComponent', () => {
  let component: UserDetailsComponent;
  let fixture: ComponentFixture<UserDetailsComponent>;
  let userService: UserService;
  let accountService: AccountService;
  let emailVerificationService: EmailVerificationService
  let router: Router;
  let infoService: InfoService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        ReactiveFormsModule,
        InfoModule,
        HttpClientTestingModule,
      ],
      declarations: [UserDetailsComponent],
      providers: [UserService, AccountService,
        EmailVerificationService,
        InfoService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    userService = TestBed.get(UserService);
    accountService = TestBed.get(AccountService);
    infoService = TestBed.get(InfoService);
    emailVerificationService = TestBed.get(EmailVerificationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user by Id', () => {
      let mockUser: User =
        {
            id: 'XXXXXX',
            email: 'tes@adorsys.de',
            login: 'bob',
            branch: '',
            pin: '12345',
            scaUserData: {},
            accountAccesses: {},
            branchLogin: 'branchLogin',
        } as User;
      let getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));
      component.getUserById();
      expect(getUserSpy).toHaveBeenCalled();
      expect(component.user).toEqual(mockUser);
  });

    it('should show confirmation letter feedback on successful confirm email', () => {
        const email = 'foo@foo.de';
        const getEmailSpy = spyOn(emailVerificationService, 'sendEmailForVerification').and.returnValue(of(''));
        const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');
        component.confirmEmail(`${email}`);
        expect(infoServiceOpenFeedbackSpy).toHaveBeenCalledWith(`Confirmation letter has been sent to your email ${email}!`)
    });

    it('should show failure message on unsuccessful confirm email ', () => {
        const email = 'foo@foo.de';
        const getEmailSpy = spyOn(emailVerificationService, 'sendEmailForVerification').and.returnValue(throwError(''));
        const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');
        component.confirmEmail(`${email}`);
        expect(infoServiceOpenFeedbackSpy).toHaveBeenCalledWith(`Sorry, something went wrong during the process of sending the confirmation!`)
    });

    it('should load users on NgOnInit', () => {
    let mockUser: User =
      {
        id: 'XXXXXX',
        email: 'tes@adorsys.de',
        login: 'bob',
        branch: '',
        pin: '12345',
        scaUserData: {},
        accountAccesses: {},
        branchLogin: 'branchLogin'
      } as User;
    let getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));

    component.ngOnInit();

    expect(getUserSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
  });

  it('should handle Click Iban', () => {
      const accountId = 'abc232';
      let getAccountSpy = spyOn(accountService, 'getAccountByIban').and.returnValue(of({id: accountId}));
      let navigateSpy = spyOn(router, 'navigate');
      const clickEvent = {target: {innerHTML: 'DE980000000001'}};
      component.handleClickOnIBAN(clickEvent);
      expect(getAccountSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['/accounts/', accountId]);
  });

    it('should confirm the email', () => {
        component.confirmEmail('tpp@tpp.de');
    });
});
