import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmPasswordComponent} from './confirm-password.component';
import {DebugElement} from "@angular/core";
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {By} from "@angular/platform-browser";
import {AuthService} from "../common/services/auth.service";

describe('ConfirmPasswordComponent', () => {
    let component: ConfirmPasswordComponent;
    let fixture: ComponentFixture<ConfirmPasswordComponent>;
    let authService: AuthService;
    let authServiceSpy;
    let de: DebugElement;
    let el: HTMLElement;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                BrowserAnimationsModule,
            ],
            providers: [AuthService],
            declarations: [ConfirmPasswordComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(ConfirmPasswordComponent);
        component = fixture.componentInstance;
        authService = fixture.debugElement.injector.get(AuthService);

        de = fixture.debugElement.query(By.css('form'));
        el = de.nativeElement;

        fixture.detectChanges();
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

        expect(authServiceSpy).toHaveBeenCalledWith({newPassword: '12345', code: '12345678'});
        expect(authServiceSpy).toHaveBeenCalled();
    });

    it('confirmNewPasswordForm should be invalid when at least one field is empty', () => {
        expect(component.confirmNewPasswordForm.valid).toBeFalsy();
    });

    it('New password field validity', () => {
        let errors = {};
        const confirmNewPassword = component.confirmNewPasswordForm.controls['newPassword'];
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
});
