import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientModule} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";

import {LoginComponent} from './login.component';


fdescribe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let de: DebugElement;
    let el: HTMLElement;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientModule
            ],

            declarations: [LoginComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;

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

        // login field is required
        errors = login.errors || {};
        expect(errors['required']).toBeTruthy();

        // set login to something correct
        login.setValue('test@test.de');
        errors = login.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('pin field validity', () => {
        let errors = {};
        const pin = component.loginForm.controls['pin'];
        expect(pin.valid).toBeFalsy();

        // pin field is required
        errors = pin.errors || {};
        expect(errors['required']).toBeTruthy();

        // set pin to something correct
        pin.setValue('12345678');
        errors = pin.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('Should set error message', () => {
        component.onSubmit();
        expect(component.errorMessage).toEqual('Please enter your credentials');
    });

/*    it('Set error message by button click and invalid form', () => {
        el = fixture.debugElement.query(By.css('button')).nativeElement;
        el.click();
        expect(component.errorMessage).toEqual('Please enter your credentials');
    });*/
});
