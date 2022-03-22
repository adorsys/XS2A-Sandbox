import { Component, Input, OnInit } from '@angular/core';
import { PageNavigationService } from '../../../services/page-navigation.service';
import { PiisConsent, User } from '../../../models/user.model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { map, takeUntil } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountAccess } from '../../../models/account-access.model';
import { PiisConsentService } from '../../../services/piis-consent.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-user-create-funds-confirmation',
  templateUrl: './user-create-funds-confirmation.component.html',
  styleUrls: ['./user-create-funds-confirmation.component.scss'],
})
export class UserCreateFundsConfirmationComponent implements OnInit {
  user: User;
  userId: string;
  iban: any;
  createFundsFormGroup: FormGroup;
  ibanList?: String[];
  private unsubscribe$ = new Subject<void>();
  todayString: string;
  errorMessage: string;
  private errorText = 'Invalid password for user';
  private errorTextNoDepositAccount = 'Please create an Deposit Account. You are only able to create a consent for the existing account.';
  showCreateDepositAccountButton: boolean = false;

  constructor(
    public pageNavigationService: PageNavigationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private piisService: PiisConsentService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
    this.createFundsFormGroup = this.formBuilder.group({
      password: ['', Validators.required],
      iban: ['', Validators.required],
      tppAuthorisationNumber: ['', Validators.required],
      validUntil: ['', Validators.required],
    });

    this.activatedRoute.params
      .pipe(
        map((response) => {
          return response.id;
        })
      )
      .subscribe((id: string) => {
        this.userId = id;
        this.getUserDetails();
      });
  }

  getUserDetails() {
    this.userService.getUser(this.userId).subscribe((item: User) => {
      this.user = item;
      console.log(this.user);
      if (this.user.accountAccesses.length >= 1) {
        this.ibanList = this.user.accountAccesses.map((access) => access.iban + ' ' + access.currency);
        this.createFundsFormGroup.get('iban').setValue(this.ibanList[0]);
      } else {
        this.errorMessage = this.errorTextNoDepositAccount;
        this.showCreateDepositAccountButton = true;
      }
    });
  }

  handleClickOnBackButton() {
    this.pageNavigationService.setLastVisitedPage(`user/${this.user.id}/update-user-details/`);
    this.router.navigate([`users/${this.user.id}`]);
  }

  onSubmit() {
    /*Execute Jsoninput */
    const password = this.createFundsFormGroup.get('password').value;
    const piisConsent = new PiisConsent();
    piisConsent.validUntil = this.createFundsFormGroup.get('validUntil').value;
    piisConsent.tppAuthorisationNumber = this.createFundsFormGroup.get('tppAuthorisationNumber').value;
    piisConsent.access = new AccountAccess();
    piisConsent.access.iban = this.createFundsFormGroup.get('iban').value.split(' ')[0];
    piisConsent.access.currency = this.createFundsFormGroup.get('iban').value.split(' ')[1];

    if (new Date(piisConsent.validUntil) >= new Date()) {
      this.piisService.createPiisConsent(piisConsent, this.user.login, password).subscribe(
        (res) => {
          this.handleClickOnBackButton();
        },
        (error: HttpErrorResponse) => {
          if (error.status === 401 && error.error.message.match(this.errorText)) {
            this.errorMessage = error.error ? error.error.message : error.message;
          }
        }
      );
    } else {
      this.errorMessage = ' Please choose a valid date in the future!';
    }
  }
}
