import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';
import { ADMIN_KEY } from 'src/app/commons/constant/constant';
import browser from 'browser-detect';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string;
  horizontalPosition: MatSnackBarHorizontalPosition = 'right';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  durationInSeconds = 4;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    public customizeService: CustomizeService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const result = browser();
    if (
      result.name !== 'chrome' &&
      result.name !== 'edge' &&
      result.name !== 'safari' &&
      result.name !== 'firefox'
    ) {
      this._snackBar.open(
        'You are using an old browser. This can lead to broken views.',
        'Close',
        {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: this.durationInSeconds * 1000,
        }
      );
    }
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter your credentials';
      return;
    }

    this.authService.login(this.loginForm.value).subscribe(
      (success) => {
        if (success) {
          this.navigateOnLogin();
        }
      },
      (data) => {
        if (data.status === 401) {
          this.errorMessage = 'Invalid credentials';
        }
      }
    );
  }

  navigateOnLogin() {
    if (sessionStorage.getItem(ADMIN_KEY) === 'true') {
      this.router.navigate(['/management']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
