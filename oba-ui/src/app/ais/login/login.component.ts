import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AisService} from "../../common/services/ais.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {ShareDataService} from "../../common/services/share-data.service";
import {RoutingPath} from "../../common/models/routing-path.model";
import {PSUAISService} from "../../api/services/psuais.service";
import LoginUsingPOSTParams = PSUAISService.LoginUsingPOSTParams;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  invalidCredentials: boolean;

  private encryptedConsentId: string;
  private redirectId: string;

  private subscriptions: Subscription[] = [];


  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private shareService: ShareDataService,
              private aisService: AisService) {
  }

  ngOnInit() {
    this.initLoginForm();

    this.getAisAuthCode();
  }

  public onSubmit(): void {
    this.subscriptions.push(
      this.aisService.aisAuthorise({
        ...this.loginForm.value,
        encryptedConsentId: this.encryptedConsentId,
        authorisationId: this.redirectId,
      } as LoginUsingPOSTParams).subscribe(authorisationResponse => {
        console.log(authorisationResponse);
        this.shareService.changeData(authorisationResponse);
        this.router.navigate([`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.GRANT_CONSENT}`]);
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private getAisAuthCode(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.encryptedConsentId = params['encryptedConsentId'];
      this.redirectId = params['redirectId'];

      this.subscriptions.push(
        this.aisService.aisAuthCode({encryptedConsentId: this.encryptedConsentId, redirectId: this.redirectId})
          .subscribe(authCodeResponse => {
              this.shareService.changeData(authCodeResponse);
            },
            (error) => {
              console.log(error);
            })
      );
    })
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }

}
