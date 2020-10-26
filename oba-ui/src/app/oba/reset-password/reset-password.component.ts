import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../common/services/auth.service';
import { CustomizeService } from '../../common/services/customize.service';
import {InfoService} from "../../common/info/info.service";

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
      public customizeService: CustomizeService,
      private authService: AuthService,
      private formBuilder: FormBuilder,
      private router: Router,
      private infoService: InfoService,
      ) {
    }

    ngOnInit() {
        this.resetPasswordForm = this.formBuilder.group({
            login: ['', Validators.required],
        });
    }

    onSubmit() {
        if (this.resetPasswordForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please enter your credentials';
            return;
        }

      this.authService.resetPasswordViaEmail(this.resetPasswordForm.value.login).subscribe(() => {
        this.infoService.openFeedback('Link for password reset was sent, check email.', {
          severity: 'info',
        });
        this.router.navigate(['/logout']);
      });
    }
}
