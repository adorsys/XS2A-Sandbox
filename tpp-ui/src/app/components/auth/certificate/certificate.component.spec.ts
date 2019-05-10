import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";

import {CertificateComponent} from './certificate.component';


describe('CertificateComponent', () => {
    let component: CertificateComponent;
    let fixture: ComponentFixture<CertificateComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientModule,
            ],
            declarations: [CertificateComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CertificateComponent);
        component = fixture.componentInstance;

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('certificateFormGroup should be valid by default', () => {
        expect(component.certificateFormGroup.valid).toBeTruthy();
    });

    // Because we hardcoded the default value
    it('certificateFormGroup should be invalid when at least one field is empty', () => {
        component.certificateFormGroup.controls['authorizationNumber'].setValue('');
        expect(component.certificateFormGroup.valid).toBeFalsy();
    });

    it('authorizationNumber field validity', () => {
        let errors = {};
        const authorizationNumber = component.certificateFormGroup.controls['authorizationNumber'];
        expect(authorizationNumber.valid).toBeTruthy();

        // authorizationNumber field is required
        errors = authorizationNumber.errors || {};
        expect(errors['required']).toBeFalsy();

        // set authorizationNumber to something incorrect
        authorizationNumber.setValue('');
        errors = authorizationNumber.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('organizationName field validity', () => {
        let errors = {};
        const organizationName = component.certificateFormGroup.controls['organizationName'];
        expect(organizationName.valid).toBeTruthy();

        // organizationName field is required
        errors = organizationName.errors || {};
        expect(errors['required']).toBeFalsy();

        // set organizationName to something incorrect
        organizationName.setValue('');
        errors = organizationName.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('commonName field validity', () => {
        let errors = {};
        const commonName = component.certificateFormGroup.controls['commonName'];
        expect(commonName.valid).toBeTruthy();

        // commonName field is required
        errors = commonName.errors || {};
        expect(errors['required']).toBeFalsy();

        // set commonName to something correct
        commonName.setValue('');
        errors = commonName.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('Germany field validity', () => {
        let errors = {};
        const Germany = component.certificateFormGroup.controls['countryName'];
        expect(Germany.valid).toBeTruthy();

        // Germany field is required
        errors = Germany.errors || {};
        expect(errors['required']).toBeFalsy();

        // set Germany to something incorrect
        Germany.setValue('');
        errors = Germany.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('domainComponent field validity', () => {
        let errors = {};
        const domainComponent = component.certificateFormGroup.controls['domainComponent'];
        expect(domainComponent.valid).toBeTruthy();

        // domainComponent field is required
        errors = domainComponent.errors || {};
        expect(errors['required']).toBeFalsy();

        // set domainComponent to something incorrect
        domainComponent.setValue('');
        errors = domainComponent.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('localityName field validity', () => {
        let errors = {};
        const localityName = component.certificateFormGroup.controls['localityName'];
        expect(localityName.valid).toBeTruthy();

        // localityName field is required
        errors = localityName.errors || {};
        expect(errors['required']).toBeFalsy();

        // set localityName to something incorrect
        localityName.setValue('');
        errors = localityName.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('organizationUnit field validity', () => {
        let errors = {};
        const organizationUnit = component.certificateFormGroup.controls['organizationUnit'];
        expect(organizationUnit.valid).toBeTruthy();

        // organizationUnit field is required
        errors = organizationUnit.errors || {};
        expect(errors['required']).toBeFalsy();

        // set organizationUnit to something incorrect
        organizationUnit.setValue('');
        errors = organizationUnit.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('stateOrProvinceName field validity', () => {
        let errors = {};
        const stateOrProvinceName = component.certificateFormGroup.controls['stateOrProvinceName'];
        expect(stateOrProvinceName.valid).toBeTruthy();

        // stateOrProvinceName field is required
        errors = stateOrProvinceName.errors || {};
        expect(errors['required']).toBeFalsy();

        // set stateOrProvinceName to something incorrect
        stateOrProvinceName.setValue('');
        errors = stateOrProvinceName.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('validity field validity', () => {
        let errors = {};
        const validity = component.certificateFormGroup.controls['validity'];
        expect(validity.valid).toBeTruthy();

        // validity field is required
        errors = validity.errors || {};
        expect(errors['required']).toBeFalsy();

        // set validity to something incorrect
        validity.setValue('abc');
        errors = validity.errors || {};
        expect(errors['pattern']).toBeTruthy();

        // set validity to something incorrect
        validity.setValue('');
        errors = validity.errors || {};
        expect(errors['required']).toBeTruthy();
    });

    it('roles field validity', () => {
        let errors = {};
        const roles = component.certificateFormGroup.controls['roles'];
        expect(roles.valid).toBeTruthy();
        expect(component.rolesOptionsError).toBeFalsy();

        // validity field is required
        errors = roles.errors || {};
        expect(errors['required']).toBeFalsy();

        // set validity to something incorrect
        roles.setValue(['', '', '']);
        component.getSelectedCheckboxValue();
        expect(component.rolesOptionsError).toBeTruthy();
    });
});
