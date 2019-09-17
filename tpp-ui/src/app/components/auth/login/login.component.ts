import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['../auth.component.scss']
})
export class LoginComponent implements OnInit {

    loginForm: FormGroup;
    errorMessage: string;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder,
        private router: Router,
        public customizeService: CustomizeService) {
    }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required],
            pin: ['', Validators.required]
        });
    }

    onSubmit() {
        if (this.loginForm.invalid) {
            this.errorMessage = 'Please enter your credentials';
            return;
        }

        this.authService.login(this.loginForm.value)
            .subscribe(success => {
                if (success) {
                    this.router.navigate(['/']);
                } else {
                    this.errorMessage = 'Invalid credentials'
                }
            });
    }
}
