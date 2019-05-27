import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {RouterTestingModule} from "@angular/router/testing";
import {Router} from "@angular/router";
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Observable} from "rxjs";

import {InfoModule} from "../../../commons/info/info.module";
import {RegisterComponent} from './register.component';
import {CertificateComponent} from "../certificate/certificate.component";
import {AuthService} from "../../../services/auth.service";

describe('RegisterComponent', () => {
    let component: RegisterComponent;
    let registerFixture: ComponentFixture<RegisterComponent>;
    let authService: AuthService;
    let router: Router;

    let de: DebugElement;
    let el: HTMLElement;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                BrowserAnimationsModule,
                InfoModule,
            ],
            providers: [AuthService],
            declarations: [RegisterComponent, CertificateComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        registerFixture = TestBed.createComponent(RegisterComponent);
        component = registerFixture.componentInstance;
        authService = TestBed.get(AuthService);
        router = TestBed.get(Router);
        de = registerFixture.debugElement.query(By.css('form'));
        el = de.nativeElement;
        registerFixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('userForm should be invalid when at least one field is empty', () => {
        expect(component.userForm.valid).toBeFalsy();
    });

    it('branch field validity', () => {
        let errors = {};
        const branch = component.userForm.controls['branch'];
        expect(branch.valid).toBeFalsy();

        // branch field is required
        errors = branch.errors || {};
        expect(errors['required']).toBeTruthy();

        // set branch to something correct
        branch.setValue('foo');
        errors = branch.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('login field validity', () => {
        let errors = {};
        const login = component.userForm.controls['login'];
        expect(login.valid).toBeFalsy();

        // login field is required
        errors = login.errors || {};
        expect(errors['required']).toBeTruthy();

        // set login to something correct
        login.setValue('test@test.de');
        errors = login.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('email field validity', () => {
        const email = component.userForm.controls['email'];

        // set email to something correct
        email.setValue('test@test.de');
       let  errors = email.errors || {};
        expect(errors['email']).toBeFalsy();
    });

    it('pin field validity', () => {
        let errors = {};
        const pin = component.userForm.controls['pin'];
        expect(pin.valid).toBeFalsy();

        // pin field is required
        errors = pin.errors || {};
        expect(errors['required']).toBeTruthy();

        // set pin to something correct
        pin.setValue('12345678');
        errors = pin.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it(`Submit button should be enabled`, () => {
        component.userForm.controls['branch'].setValue('12345678');
        component.userForm.controls['login'].setValue('test');
        component.userForm.controls['email'].setValue('asd@asd.com');
        component.userForm.controls['pin'].setValue('1234');

        registerFixture.detectChanges();
        el = registerFixture.debugElement.query(By.css('button')).nativeElement.disabled;
        expect(el).toBeFalsy();
    });

    it('should register and redirect user', () => {
        component.userForm.controls['branch'].setValue('12345678');
        component.userForm.controls['login'].setValue('test');
        component.userForm.controls['email'].setValue('asd@asd.com');
        component.userForm.controls['pin'].setValue('1234');
        expect(component.generateCertificate).toBeFalsy();
        expect(component.userForm.valid).toBeTruthy();

        // submit form
        let registerSpy = spyOn(authService, 'register').and.callFake(() => Observable.of({value: "sample response"}));
        let navigateSpy = spyOn(router, 'navigate').and.callFake(() => Promise.resolve([]));
        component.onSubmit();
        expect(registerSpy).toHaveBeenCalled();
         expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });
});
