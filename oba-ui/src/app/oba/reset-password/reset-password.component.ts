import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../common/services/auth.service';

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
    resetPasswordForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder,
        private router: Router) {
    }

    ngOnInit() {
        this.resetPasswordForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)),]],
            login: ['', Validators.required],
        });
    }

    onSubmit() {
        if (this.resetPasswordForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please enter your credentials';
            return;
        }

        this.authService.requestCodeToResetPassword(this.resetPasswordForm.value)
            .subscribe(() => this.router.navigate(['/confirm-password']));
    }
}
