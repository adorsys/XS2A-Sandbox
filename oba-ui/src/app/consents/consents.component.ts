import { Component, OnInit } from '@angular/core';
import { OnlineBankingAISService } from '../api/services';
import { AisAccountConsent } from '../api/models';
import { AuthService } from '../common/services/auth.service';

@Component({
  selector: 'app-consents',
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.scss']
})
export class ConsentsComponent implements OnInit {

  consents: AisAccountConsent[] = [];

  constructor(
    private onlineBankingAISService: OnlineBankingAISService,
    private authService: AuthService) {}

  ngOnInit() {
    this.onlineBankingAISService.consentsUsingGETResponse(this.authService.getAuthorizedUser()).subscribe(consents => {
      this.consents = consents.body;
    });
  }

}
