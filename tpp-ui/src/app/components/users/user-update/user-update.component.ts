import {Component, OnInit} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {User} from "../../../models/user.model";
import {ActivatedRoute, Router} from "@angular/router";
import {map} from "rxjs/operators";
import {ScaMethods} from "../../../models/scaMethods";

@Component({
    selector: 'app-user-update',
    templateUrl: './user-update.component.html',
    styleUrls: ['./user-update.component.scss']
})
export class UserUpdateComponent implements OnInit {
    user : User;
    updateUserForm: FormGroup;
    methods: string[];

    userId: string;
    public submitted: boolean;
    public errorMessage: string;

    constructor(private userService: UserService,
                private formBuilder: FormBuilder,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
        this.user = new User();
    }

    ngOnInit() {
        this.setupUserFormControl();
        this.activatedRoute.params
            .pipe(
                map(response => {
                    return response.id;
                })
            )
            .subscribe((id: string) => {
                this.userId = id;
                this.getMethodsValues();
                this.getUserDetails();
            });
    }

    setupUserFormControl(): void {
        this.updateUserForm = this.formBuilder.group({
            scaUserData: this.formBuilder.array([]),
            email: ['', [Validators.required, Validators.email]],
            login: ['', Validators.required],
            pin: ['', [Validators.required, Validators.minLength(5)]],
        });
    }

    get formControl() {
        return this.updateUserForm.controls;
    }

    onSubmit() {
        this.submitted = true;
        if (this.updateUserForm.invalid) {
            this.errorMessage = 'Please verify your credentials';
            return;
        }

        const updatedUser: User = {
            ...this.user,
            email: this.updateUserForm.get('email').value,
            login: this.updateUserForm.get('login').value,
            pin: this.updateUserForm.get('pin').value,
            scaUserData: this.updateUserForm.get('scaUserData').value
        };

        this.userService.updateUserDetails(updatedUser)
            .subscribe(() => this.router.navigate(['/users/all'])
        );
    }

    initScaData() {
        const emailValidators = [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/))];

        const scaData = this.formBuilder.group({
            id: '',
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
            if (value === ScaMethods.EMAIL){
                scaData.get('methodValue').setValidators(emailValidators);
            } else if (value === ScaMethods.MOBILE){
                scaData.get('methodValue').setValidators([Validators.required, Validators.pattern(new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/))]);
            } else {
                scaData.get('methodValue').setValidators([Validators.required]);
            }
            scaData.get('methodValue').updateValueAndValidity();
        });

        return scaData;
    }

    getUserDetails() {
        this.userService.getUser(this.userId).subscribe((item: User) => {
            this.user = item;
            this.updateUserForm.patchValue({
                email: this.user.email,
                pin: this.user.pin,
                login: this.user.login,
            });
            const scaUserData = <FormArray>this.updateUserForm.get('scaUserData');
            this.user.scaUserData.forEach((value, i) => {
                if (scaUserData.length < i + 1) {
                    scaUserData.push(this.initScaData());
                }
                scaUserData.at(i).patchValue(value);
            });
        });
    }

    addScaDataItem() {
        const control = <FormArray>this.updateUserForm.controls['scaUserData'];
        control.push(this.initScaData());
    }

    removeScaDataItem(i: number) {
        const control = <FormArray>this.updateUserForm.controls['scaUserData'];
        control.removeAt(i);
    }

     getMethodsValues() {
         this.methods = Object.keys(ScaMethods);
    }

}
