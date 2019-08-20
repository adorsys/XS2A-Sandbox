import { Component, OnInit } from '@angular/core';
import { AisAccountConsent } from '../api/models';
import { OnlineBankingService } from '../common/services/online-banking.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-consents',
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.scss']
})
export class ConsentsComponent implements OnInit {

  consents: AisAccountConsent[] = [];

  constructor(
    private onlineBankingService: OnlineBankingService) {}

  ngOnInit() {
    this.onlineBankingService.getConsents().pipe(
      map(consents => consents.map(consent => consent.aisAccountConsent))
    ).subscribe(consents => {
      this.consents = consents;
    });
  }

}
