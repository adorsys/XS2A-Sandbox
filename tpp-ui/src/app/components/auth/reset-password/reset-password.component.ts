import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

import {AuthService} from '../../../services/auth.service';
import {CustomizeService} from '../../../services/customize.service';
import {TppUserService} from "../../../services/tpp.user.service";
import { InfoService } from '../../../commons/info/info.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['../auth.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    public customizeService: CustomizeService,
    private tppUserService: TppUserService,
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
      this.errorMessage = 'Please enter valid login';
      return;
    }

    this.tppUserService.resetPasswordViaEmail(this.resetPasswordForm.value.login).subscribe(() => {
      this.infoService.openFeedback('Link for password reset was sent, check email.', {
        severity: 'info',
      });
      this.router.navigate(['/logout']);
    });
  }
}
