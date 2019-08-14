import { Component, OnInit } from '@angular/core';
import { AisAccountConsent } from '../api/models';
import { OnlineBankingService } from '../common/services/online-banking.service';

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
    this.onlineBankingService.getConsents().subscribe(consents => {
      this.consents = consents;
    });
  }

}
