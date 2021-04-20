import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { CustomizeService } from '../../common/services/customize.service';
import { AuthService } from '../../common/services/auth.service';
import { ConfirmPasswordComponent } from './confirm-password.component';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { LoginComponent } from '../login/login.component';

describe('ConfirmPasswordComponent', () => {
  let component: ConfirmPasswordComponent;
  let fixture: ComponentFixture<ConfirmPasswordComponent>;
  let authService: AuthService;
  let customizeService: CustomizeService;
  let authServiceSpy;
  let de: DebugElement;
  let el: HTMLElement;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          HttpClientTestingModule,
          RouterTestingModule.withRoutes([
            { path: 'login', component: LoginComponent },
          ]),
          BrowserAnimationsModule,
        ],
        providers: [AuthService, CustomizeService],
        declarations: [ConfirmPasswordComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmPasswordComponent);
    component = fixture.componentInstance;
    authService = fixture.debugElement.injector.get(AuthService);
    customizeService = TestBed.inject(CustomizeService);
    de = fixture.debugElement.query(By.css('form'));
    el = de.nativeElement;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    router.initialNavigation();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call changePassword on the service', () => {
    authServiceSpy = spyOn(authService, 'resetPassword').and.callThrough();

    const form = component.confirmNewPasswordForm;
    form.controls['newPassword'].setValue('12345');
    form.controls['code'].setValue('12345678');
    fixture.detectChanges();

    el = fixture.debugElement.query(By.css('button')).nativeElement;
    el.click();

    expect(authServiceSpy).toHaveBeenCalledWith({
      newPassword: '12345',
      code: '12345678',
    });
    expect(authServiceSpy).toHaveBeenCalled();
  });

  it('confirmNewPasswordForm should be invalid when at least one field is empty', () => {
    expect(component.confirmNewPasswordForm.valid).toBeFalsy();
  });

  it('New password field validity', () => {
    let errors = {};
    const confirmNewPassword =
      component.confirmNewPasswordForm.controls['newPassword'];
    expect(confirmNewPassword.valid).toBeFalsy();

    // confirmNewPassword field is required
    errors = confirmNewPassword.errors || {};
    expect(errors['required']).toBeTruthy();
    fixture.detectChanges();

    // set confirmNewPassword to something correct
    confirmNewPassword.setValue('123345');
    errors = confirmNewPassword.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('code field validity', () => {
    let errors = {};
    const code = component.confirmNewPasswordForm.controls['code'];
    expect(code.valid).toBeFalsy();

    // code field is required
    errors = code.errors || {};
    expect(errors['required']).toBeTruthy();

    // set code to something correct
    code.setValue('12345678');
    errors = code.errors || {};
    fixture.detectChanges();
    expect(errors['required']).toBeFalsy();
  });

  it('should call the valid form on Submit ', () => {
    component.confirmNewPasswordForm.get('newPassword').setValue('12345');
    component.confirmNewPasswordForm.get('code').setValue('1234');
    const resetSpy = spyOn(authService, 'resetPassword').and.returnValue(
      of({})
    );
    component.onSubmit();
    expect(resetSpy).toHaveBeenCalled();
  });

  /* it('should call the on Submit', () => {
    component.onSubmit();
    expect(component.confirmNewPasswordForm.invalid).toBeTruthy();
  }); */
});
