import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../common/services/auth.service';

@Component({
  selector: 'app-confirm-password',
  templateUrl: './confirm-password.component.html',
  styleUrls: ['./confirm-password.component.scss']
})
export class ConfirmPasswordComponent implements OnInit {

    confirmNewPasswordForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder,
        private router: Router) {
    }

    ngOnInit() {
        this.confirmNewPasswordForm = this.formBuilder.group({
            newPassword: ['', Validators.required],
            code: ['', Validators.required],
        });
    }

    onSubmit() {
        if (this.confirmNewPasswordForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please enter your credentials';
            return;
        }

        this.authService.resetPassword(this.confirmNewPasswordForm.value)
            .subscribe(() => this.router.navigate(['/login']));
    }

}
