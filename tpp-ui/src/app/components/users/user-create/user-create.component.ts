import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {User} from "../../../models/user.model";
import {InfoService} from "../../../commons/info/info.service";
import {ScaMethods} from "../../../models/scaMethods";

@Component({
    selector: 'app-user-create',
    templateUrl: './user-create.component.html',
    styleUrls: ['./user-create.component.scss']
})
export class UserCreateComponent implements OnInit {

    id: string;
    user: User;
    methods: string[];

    userForm: FormGroup;
    submitted: boolean;

    constructor(
        private userService: UserService,
        private formBuilder: FormBuilder,
        private router: Router,
        private infoService: InfoService,
        private route: ActivatedRoute) {
    }

    get formControl() {
        return this.userForm.controls;
    }

    ngOnInit() {
        this.getMethodsValues();
        this.setupUserFormControl();
    }

    setupUserFormControl(): void {
        this.userForm = this.formBuilder.group({
            scaUserData: this.formBuilder.array([
                this.initScaData()
            ]),
            email: ['', [Validators.required, Validators.email]],
            login: ['', Validators.required],
            pin: ['', [Validators.required, Validators.minLength(5)]],
            userRoles: this.formBuilder.array(['CUSTOMER']) // register users with customer role
        });
    }

    initScaData() {
        const emailValidators = [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/))];

        const scaData = this.formBuilder.group({
            scaMethod: ['', Validators.required],
            methodValue: [''],
            staticTan: [{value: '', disabled: true}],
            usesStaticTan: [false]
        });

        scaData.get('usesStaticTan').valueChanges.subscribe((bool: boolean = true) => {
            if(bool) {
                scaData.get('staticTan').setValidators(Validators.required);
                scaData.get('staticTan').enable();
            } else {
                scaData.get('staticTan').clearValidators();
                scaData.get('staticTan').disable();
                scaData.get('staticTan').setValue('');
            }
            scaData.get('staticTan').updateValueAndValidity();
        });

        scaData.get('scaMethod').valueChanges.subscribe(value => {
            if (value === ScaMethods.EMAIL) {
                scaData.get('methodValue').setValidators(emailValidators);
            } else if (value === ScaMethods.MOBILE) {
                scaData.get('methodValue').setValidators([Validators.required, Validators.pattern(new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/))]);
            } else {
                scaData.get('methodValue').setValidators([Validators.required]);
            }
            scaData.get('methodValue').updateValueAndValidity();
        });
        return scaData;
    }

    addScaDataItem() {
        const control = <FormArray>this.userForm.controls['scaUserData'];
        control.push(this.initScaData());
    }

    removeScaDataItem(i: number) {
        const control = <FormArray>this.userForm.controls['scaUserData'];
        control.removeAt(i);
    }

    onSubmit() {
        this.submitted = true;

        if (this.userForm.invalid) {
            return;
        }

        this.userService.createUser(this.userForm.value)
            .subscribe(() => {
                this.router.navigateByUrl('/users/all');
            }, () => {
                this.infoService.openFeedback("Provided Login or Email are already taken", {
                    severity: 'error'
                });
            });
    }

    getMethodsValues() {
        this.methods = Object.keys(ScaMethods);
    }
}
