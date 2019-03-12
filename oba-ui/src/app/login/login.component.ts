import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {first} from "rxjs/operators";
import {RoutingPath} from "../common/models/routing-path.model";
import {AisService} from "../common/services/ais.service";
import {URL_PARAMS_PROVIDER} from "../common/constants/constants";
import {HttpHeaders} from "@angular/common/http";

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
    Cookie = 'eyJraWQiOiJNQmxEd3d1NlJ4RXJ1dGhmc0QycnBZIiwiYWxnIjoiSFMyNTYifQ.eyJlbmMtY29uc2VudC1pZCI6IlA1a1p1bTZpMFZYT3E5bnNFNmcxaVNXT3RPTUxnd2c1OUtIdThVRm9ZcUJTYmRtMnR3UUhfTEJURzk0bFRJNnVTZmdpcWxER0VmWWI3aFhPeXpfUDlnPT1fPV9iUzZwNlh2VFdJIiwiY29uc2VudC10eXBlIjoiQUlTIiwicmVkaXJlY3QtaWQiOiI1ODc0YTQ4NC01NWUwLTRmNTQtYTYzYS0zN2JkNjg3YjAzMDEiLCJleHAiOjE1NTIzOTE2MDgsImlhdCI6MTU1MjM5MTMwOCwianRpIjoiX0pvX3VGaHFRdW92M0doWEZBaHV2dyJ9.4aJTNRHGg4gZGFcAlIVo-MizWwmotSik5Opf-a3I84w';
    header = new HttpHeaders();

    constructor(private formBuilder: FormBuilder,
                private router: Router,
                private route: ActivatedRoute,
                private psuAisService: AisService,
                @Inject(URL_PARAMS_PROVIDER) params) {
        this.encryptedConsentId = params['encryptedConsentId'];
        this.authorisationId = params['authorisationId'];
        console.log('params: ', params);
    }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required],
            pin: ['', Validators.required]
        });
        this.header.append("Cookie", this.Cookie);
    }

    onSubmit() {
        this.loading = true;
        this.psuAisService.loginUsingAuthorizationId({
            ...this.loginForm.value,
            encryptedConsentId: this.encryptedConsentId,
            authorisationId: this.authorisationId,
            headers: this.header
        })
            .pipe(first())
            .subscribe(
                data => {
                    this.router.navigate([`./${RoutingPath.BANK_OFFERED}`]);
                },
                error => {
                    this.error = error;
                    this.loading = false;
                });
    }
}
