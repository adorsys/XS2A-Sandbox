import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';

import {URL_PARAMS_PROVIDER} from '../common/constants/constants';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {LoginComponent} from './login.component';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [
                LoginComponent
            ],
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientTestingModule
            ],
            providers: [
                AisService,
                ShareDataService,
                {provide: URL_PARAMS_PROVIDER, useValue: {}}
            ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });


    it('loginForm should be invalid when at least one field is empty', () => {
        expect(component.loginForm.valid).toBeFalsy();
    });

    it('login field validity', () => {
        const login = component.loginForm.controls['login'];
        expect(login.valid).toBeFalsy();

        // psuId field is required
        let errors = login.errors || {};
        expect(errors['required']).toBeTruthy();

        // set psuId to something correct
        login.setValue('user1');
        errors = login.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('password field validity', () => {
        const password = component.loginForm.controls['pin'];
        expect(password.valid).toBeFalsy();

        // password field is required
        let errors = password.errors || {};
        expect(errors['required']).toBeTruthy();

        // set password to something correct
        password.setValue('user1');
        errors = password.errors || {};
        expect(errors['required']).toBeFalsy();
    });

    it('Should check that the Next button is deactivated when the form is invalid', () => {
        const weiterBtn = fixture.nativeElement.querySelector('button[type="submit"]');
        expect(weiterBtn.disabled).toBeTruthy();
        expect(component.loginForm.valid).toBeFalsy();
    });

    it('Should check that the Weiter button is activated when we have given psuId and password', () => {
        const weiterBtn = fixture.nativeElement.querySelector('button[type="submit"]');
        const login = component.loginForm.controls['login'];
        const pin = component.loginForm.controls['pin'];
        login.setValue('user1');
        pin.setValue('12456');
        fixture.detectChanges();
        expect(weiterBtn.disabled).toBeFalsy();
        expect(component.loginForm.valid).toBeTruthy();
    });
});
