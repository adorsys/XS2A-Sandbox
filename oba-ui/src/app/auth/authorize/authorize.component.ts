import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {CustomizeService} from "../../common/services/customize.service";
import {OnlineBankingOauthAuthorizationService} from "../../api/services/online-banking-oauth-authorization.service";
import {OauthService} from "../services/oauth.service";
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;
import {map} from "rxjs/operators";

@Component({
  selector: 'app-authorize',
  templateUrl: './authorize.component.html',
  styleUrls: ['./authorize.component.scss']
})
export class AuthorizeComponent implements OnInit {

  authorizeForm: FormGroup;
  private redirectUri: string;

  constructor(
    public customizeService: CustomizeService,
    private oAuthService: OauthService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      this.redirectUri = params.redirect_uri;

      if (params.redirect_uri == undefined) {
        this.router.navigate(['']);
      }
    });

    this.initAuthorizeForm();
  }

  public onSubmit(): void {
    this.oAuthService.authorize({
      ...this.authorizeForm.value,
      redirectUri: this.redirectUri,
    } as OauthCodeUsingPOSTParams)
      .pipe(
        map(response => response.redirectUri)
      )
      //TODO fix this line and add removed tests https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/475
      .subscribe(url => window.location.href = url);
  }

  private initAuthorizeForm(): void {
    this.authorizeForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }

}
