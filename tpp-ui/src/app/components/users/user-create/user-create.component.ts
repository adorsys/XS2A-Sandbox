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
        return this.formBuilder.group({
            scaMethod: ['EMAIL', Validators.required],
            methodValue: ['', Validators.required],
            staticTan: [''],
            usesStaticTan: ['']
        });
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