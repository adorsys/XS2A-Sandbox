import { Component, OnInit } from '@angular/core';
import {Subscription} from "rxjs";
import {ShareDataService} from "../../common/services/share-data.service";
import {PaymentAuthorizeResponse} from "../../api/models";

@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrls: ['./payment-details.component.scss']
})
export class PaymentDetailsComponent implements OnInit {

  private subscriptions: Subscription[] = [];

  public authResponse: PaymentAuthorizeResponse;

  constructor(private sharedService: ShareDataService) {}

  ngOnInit() {
    this.sharedService.currentData.subscribe(
      authResponse => this.authResponse = authResponse
    );
  }

}
