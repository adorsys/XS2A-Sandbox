import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';

@Component({
    selector: 'app-confirm-new-password',
    templateUrl: './confirm-new-password.component.html',
    styleUrls: ['../auth.component.scss']
})
export class ConfirmNewPasswordComponent implements OnInit {

    confirmNewPasswordForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder,
        private router: Router,
        public customizeService: CustomizeService) {
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

        this.authService.changePassword(this.confirmNewPasswordForm.value)
            .subscribe(() => this.router.navigate(['/login']));
    }
}
