/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { Component, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomizeService } from '../../common/services/customize.service';
import { OnlineBankingOauthAuthorizationService } from '../../api/services/online-banking-oauth-authorization.service';
import { OauthService } from '../services/oauth.service';
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-authorize',
  templateUrl: './authorize.component.html',
  styleUrls: ['./authorize.component.scss'],
})
export class AuthorizeComponent implements OnInit {
  authorizeForm: UntypedFormGroup;
  private redirectUri: string;

  constructor(
    public customizeService: CustomizeService,
    private oAuthService: OauthService,
    private formBuilder: UntypedFormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.redirectUri = params.redirect_uri;

      if (params.redirect_uri === undefined) {
        this.router.navigate(['']);
      }
    });

    this.initAuthorizeForm();
  }

  public onSubmit(): void {
    this.oAuthService
      .authorize({
        ...this.authorizeForm.value,
        redirectUri: this.redirectUri,
      } as OauthCodeUsingPOSTParams)
      .pipe(map((response) => response.redirectUri))
      .subscribe((url) => (window.location.href = url));
  }

  private initAuthorizeForm(): void {
    this.authorizeForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }
}
