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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AccountService } from '../../../services/account.service';
import { UserDetailsComponent } from './user-details.component';
import { UserService } from '../../../services/user.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../models/user.model';
import { EmailVerificationService } from '../../../services/email-verification.service';
import { InfoService } from '../../../commons/info/info.service';
import { InfoModule } from '../../../commons/info/info.module';
import { ScaUserData } from '../../../models/sca-user-data.model';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OverlayModule } from '@angular/cdk/overlay';

describe('UserDetailsComponent', () => {
  let component: UserDetailsComponent;
  let fixture: ComponentFixture<UserDetailsComponent>;
  let userService: UserService;
  let accountService: AccountService;
  let emailVerificationService: EmailVerificationService;
  let router: Router;
  let infoService: InfoService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule.withRoutes([]), ReactiveFormsModule, HttpClientTestingModule, BrowserAnimationsModule, OverlayModule],
        declarations: [UserDetailsComponent],
        providers: [UserService, AccountService, EmailVerificationService, InfoService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    userService = TestBed.inject(UserService);
    accountService = TestBed.inject(AccountService);
    infoService = TestBed.inject(InfoService);
    emailVerificationService = TestBed.inject(EmailVerificationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user by Id', () => {
    const mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '',
      pin: '12345',
      scaUserData: {},
      accountAccesses: {},
      branchLogin: 'branchLogin',
    } as User;
    const getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));
    component.getUserById();
    expect(getUserSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
  });

  it('should show confirmation letter feedback on successful confirm email', () => {
    const mockUserData: ScaUserData = {
      decoupled: false,
      id: '',
      methodValue: 'foo@foo.de',
      scaMethod: '',
      staticTan: '',
      usesStaticTan: false,
      valid: false,
    };
    const getEmailSpy = spyOn(emailVerificationService, 'sendEmailForVerification').and.returnValue(of(''));
    const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');
    component.confirmEmail(mockUserData);
    expect(infoServiceOpenFeedbackSpy).toHaveBeenCalledWith(`Confirmation letter has been sent to your email ${mockUserData.methodValue}!`);
  });

  it('should show failure message on unsuccessful confirm email ', () => {
    const mockUserData: ScaUserData = {
      decoupled: false,
      id: '',
      methodValue: 'foo@foo.de',
      scaMethod: '',
      staticTan: '',
      usesStaticTan: false,
      valid: false,
    };
    const getEmailSpy = spyOn(emailVerificationService, 'sendEmailForVerification').and.returnValue(throwError(''));
    const infoServiceOpenFeedbackSpy = spyOn(infoService, 'openFeedback');
    component.confirmEmail(mockUserData);
    expect(infoServiceOpenFeedbackSpy).toHaveBeenCalledWith(`Sorry, something went wrong during the process of sending the confirmation!`);
  });

  it('should load users on NgOnInit', () => {
    const mockUser: User = {
      id: 'XXXXXX',
      email: 'tes@adorsys.de',
      login: 'bob',
      branch: '',
      pin: '12345',
      scaUserData: {},
      accountAccesses: {},
      branchLogin: 'branchLogin',
    } as User;
    const getUserSpy = spyOn(userService, 'getUser').and.returnValue(of(mockUser));

    component.ngOnInit();

    expect(getUserSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
  });

  it('should handle Click Iban', () => {
    const accountId = 'abc232';
    const navigateSpy = spyOn(router, 'navigate');
    component.handleClickOnIBAN(accountId);
    expect(navigateSpy).toHaveBeenCalledWith(['/accounts/', accountId]);
  });

  it('should confirm the email', () => {
    const mockUserData: ScaUserData = {
      decoupled: false,
      id: '',
      methodValue: 'foo@foo.de',
      scaMethod: '',
      staticTan: '',
      usesStaticTan: false,
      valid: false,
    };
    const emailVerificationServiceSpy = spyOn(emailVerificationService, 'sendEmailForVerification').and.returnValue(of(''));
    component.confirmEmail(mockUserData);
    expect(emailVerificationServiceSpy).toHaveBeenCalledWith('foo@foo.de');
  });
});
