import { Component, OnInit } from '@angular/core';
import { PiisConsent } from '../../../models/user.model';
import { PageNavigationService } from '../../../services/page-navigation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { PiisConsentService } from '../../../services/piis-consent.service';

@Component({
  selector: 'app-user-funds-confirmation-details',
  templateUrl: './user-funds-confirmation-details.component.html',
  styleUrls: ['./user-funds-confirmation-details.component.scss'],
})
export class UserFundsConfirmationDetailsComponent implements OnInit {
  piisConsent?: PiisConsent;
  private userLogin?: string;

  constructor(
    public pageNavigationService: PageNavigationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private piisConsentService: PiisConsentService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.params
      .pipe(
        map((response) => {
          this.userLogin = response.userLogin;
          return response.id;
        })
      )
      .subscribe((id: string) => {
        /* Load consent from list, maybe userid is needed?*/
        this.piisConsentService.getPiisConsent(id, this.userLogin).subscribe((piisConsent: PiisConsent) => {
          this.piisConsent = piisConsent;
          this.piisConsent.consentId = id;
        });
      });
  }

  terminate() {
    /* PIIS consent status should be changed to terminatedByAspsp */
    /* Testing purpose */
    console.log(this.piisConsent);
    this.piisConsentService.putPiiSConsent(this.piisConsent).subscribe((res) => {
      console.log(res);
    });
    this.router.navigate([this.pageNavigationService.getLastVisitedPage()]);
  }
}
