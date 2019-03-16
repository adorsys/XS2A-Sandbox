import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {first} from "rxjs/operators";
import {AisService} from "../common/services/ais.service";
import {URL_PARAMS_PROVIDER} from "../common/constants/constants";
import {HttpHeaders} from "@angular/common/http";
import {ObaUtils} from "../common/utils/oba-utils";
import {CookieService} from "ngx-cookie-service";

@Component({
    selector: 'ais-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    loginForm: FormGroup;
    loading = false;
    error: string;
    authorisationId: string;
    encryptedConsentId: string;
    cookie: string;
    header = new HttpHeaders();

    constructor(private formBuilder: FormBuilder,
                private router: Router,
                private route: ActivatedRoute,
                private psuAisService: AisService,
                private cookieService: CookieService,
                @Inject(URL_PARAMS_PROVIDER) params) {
        this.encryptedConsentId = params.encryptedConsentId;
        this.authorisationId = params.authorisationId;

        console.log('params: ', params);
    }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required],
            pin: ['', Validators.required]
        });
    }

    onSubmit() {
        this.loading = true;
        console.log('loginUsingAuthorizationId');
        this.psuAisService.loginUsingAuthorizationId({
            ...this.loginForm.value,
            encryptedConsentId: this.encryptedConsentId,
            authorisationId: this.authorisationId,
        })
            .pipe(first())
            .subscribe(
                data => {
                    this.router.navigate(['/login', ObaUtils.getQueryParams(this.encryptedConsentId, this.authorisationId)]);
                },
                error => {
                    this.error = error;
                    this.loading = false;
                });
    }

}
