import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';
import { TppUserService } from '../../../services/tpp.user.service';
import { InfoService } from '@commons/info/info.service';
import { emailValidator } from '@commons/validators/email.validator';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  resetPasswordForm: FormGroup;
  submitted: boolean;
  errorMessage: string;

  private onDestroy = new Subject<void>();

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    public customizeService: CustomizeService,
    private tppUserService: TppUserService,
    private infoService: InfoService
  ) {}

  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      login: ['', [Validators.required, emailValidator()]],
    });
  }

  ngOnDestroy() {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      this.submitted = true;
      this.errorMessage = 'Please enter valid email';
      return;
    }

    this.tppUserService
      .resetPasswordViaEmail(this.resetPasswordForm.value.login)
      .pipe(takeUntil(this.onDestroy))
      .subscribe(() => {
        this.infoService.openFeedback(
          'Link for password reset was sent, check email.',
          {
            severity: 'info',
          }
        );
        this.router.navigate(['/logout']);
      });
  }
}
