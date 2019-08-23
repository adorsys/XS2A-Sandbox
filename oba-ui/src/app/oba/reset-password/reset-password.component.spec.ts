import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthService } from '../../common/services/auth.service';
import { ResetPasswordComponent } from './reset-password.component';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let authService: AuthService;
  let authServiceSpy;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ResetPasswordComponent ],
      imports: [
          ReactiveFormsModule,
          HttpClientTestingModule,
          RouterTestingModule,
          BrowserAnimationsModule,
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    authService = fixture.debugElement.injector.get(AuthService);

    de = fixture.debugElement.query(By.css('form'));
    el = de.nativeElement;
    fixture.detectChanges();
    component.ngOnInit();  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

    it('should call login on the service', () => {
        authServiceSpy = spyOn(authService, 'requestCodeToResetPassword').and.callThrough();

        const form = component.resetPasswordForm;
        form.controls['login'].setValue('test');
        form.controls['email'].setValue('test@test.de');
        fixture.detectChanges();

        el = fixture.debugElement.query(By.css('button')).nativeElement;
        el.click();

        expect(authServiceSpy).toHaveBeenCalledWith({login: 'test', email: 'test@test.de'});
        expect(authServiceSpy).toHaveBeenCalled();
    });

    it('loginForm should be invalid when at least one field is empty', () => {
        expect(component.resetPasswordForm.valid).toBeFalsy();
    });

    it('email field validity', () => {
        let errors = {};
        const email = component.resetPasswordForm.controls['email'];
        expect(email.valid).toBeFalsy();

        // email field is required
        errors = email.errors || {};
        expect(errors['required']).toBeTruthy();

        // set email to something correct
        email.setValue('test@test.de');
        errors = email.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('login field validity', () => {
        let errors = {};
        const login = component.resetPasswordForm.controls['login'];
        expect(login.valid).toBeFalsy();

        // login field is required
        errors = login.errors || {};
        expect(errors['required']).toBeTruthy();

        // set login to something correct
        login.setValue('foo');
        errors = login.errors || {};
        expect(errors['required']).toBeFalsy();
    });
});
