import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { OnlineBankingAuthorizationService } from '../../api/services/online-banking-authorization.service';
import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { AuthService } from '../../common/services/auth.service';

import LoginUsingPOST1Params = OnlineBankingAuthorizationService.LoginUsingPOST1Params;
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  invalidCredentials: boolean;

  private subscriptions: Subscription[] = [];


  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private authService: AuthService,
              private infoService: InfoService) {
  }

  ngOnInit() {
    this.initLoginForm();
  }

  public onSubmit(): void {
    this.subscriptions.push(
      this.authService.login({...this.loginForm.value} as LoginUsingPOST1Params)
        .subscribe(success => {
          if (success) {
            this.router.navigate([`${RoutingPath.ACCOUNTS}`]);
          } else {
            this.infoService.openFeedback('Invalid credentials', {
              severity: 'error'
            });
          }
        })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }

}
