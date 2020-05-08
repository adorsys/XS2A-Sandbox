import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

fdescribe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientModule],
      providers: [AuthService],

      declarations: [LoginComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    authService = fixture.debugElement.injector.get(AuthService);

    de = fixture.debugElement.query(By.css('form'));
    el = de.nativeElement;

    fixture.detectChanges();
    component.ngOnInit();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loginForm should be invalid when at least one field is empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('login field validity', () => {
    let errors = {};
    const login = component.loginForm.controls['login'];
    expect(login.valid).toBeFalsy();

    errors = login.errors || {};
    expect(errors['required']).toBeTruthy();

    login.setValue('test@test.de');
    errors = login.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('pin field validity', () => {
    let errors = {};
    const pin = component.loginForm.controls['pin'];
    expect(pin.valid).toBeFalsy();

    errors = pin.errors || {};
    expect(errors['required']).toBeTruthy();

    pin.setValue('12345678');
    errors = pin.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('Should set error message', () => {
    component.onSubmit();
    expect(component.errorMessage).toEqual('Please enter your credentials');
  });

  // TODO write unite tests https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/704
  it('should login and go to the next page', () => {
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    const logSpy = spyOn(authService, 'login').and.returnValue(of(true));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(logSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  });

  it('should throw a error message', () => {
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    const logSpy = spyOn(authService, 'login').and.returnValue(of(false));
    component.onSubmit();
    expect(logSpy).toHaveBeenCalled();
    expect(component.errorMessage).toEqual('Invalid credentials');
  });
});
