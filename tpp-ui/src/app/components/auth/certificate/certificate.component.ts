import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-certificate',
    templateUrl: './certificate.component.html',
    styleUrls: ['../auth.component.scss']
})
export class CertificateComponent implements OnInit {

    @Output() certificateValue = new EventEmitter();

    certificateFormGroup: FormGroup;
    rolesOptionsError: Boolean = false;

    public roles: Array<string> = ['PIISP', 'PISP', 'AISP'];
    selectedOptions = ['PIISP', 'PISP', 'AISP'];

    constructor(private formBuilder: FormBuilder) {}

    ngOnInit() {
        this.initializeCertificateGeneratorForm();
        this.onChange();
    }

    addCheckboxControls() {
        const arr = this.roles.map(() => {
            return this.formBuilder.control(true);
        });
        return this.formBuilder.array(arr);
    }

    getSelectedCheckboxValue() {
        this.selectedOptions = [];
        this.checkboxArray.controls.forEach((control, i) => {
            if (control.value) {
                this.selectedOptions.push(this.roles[i]);
            }
        });
        this.rolesOptionsError = this.selectedOptions.length <= 0;
    }

    onChange() {
        this.certificateFormGroup.value.roles = this.selectedOptions;
        const status = this.certificateFormGroup.valid && !this.rolesOptionsError;
        if (status) {
            this.certificateValue.emit(this.certificateFormGroup.value);
        } else {
            this.certificateValue.emit(false);
        }
    }

    public initializeCertificateGeneratorForm(): void {
        this.certificateFormGroup = this.formBuilder.group({
            authorizationNumber: ['ID12345', Validators.required],
            organizationName: ['Awesome TPP', Validators.required],
            countryName: ['Germany', Validators.required],
            domainComponent: ['awesome-tpp.de', Validators.required],
            localityName: ['Nuremberg', Validators.required],
            organizationUnit: ['IT department', Validators.required],
            stateOrProvinceName: ['Bayern', Validators.required],
            validity: ['365',
                [
                    Validators.required,
                    Validators.pattern("^[0-9]*$"),
                ]
            ],
            roles: this.addCheckboxControls(),
        });
    }

    get checkboxArray() {
        return <FormArray>this.certificateFormGroup.get('roles');
    }
}
