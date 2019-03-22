import { Component, OnInit } from '@angular/core';
import {ShareDataService} from "../../../common/services/share-data.service";

@Component({
  selector: 'app-bank-offered',
  templateUrl: './bank-offered.component.html',
  styleUrls: ['./bank-offered.component.scss']
})
export class BankOfferedComponent implements OnInit {

  constructor(private sharedService: ShareDataService) { }

  ngOnInit() {
    this.sharedService.getCurrentConsentAuthorizeResponse().subscribe(response => {
      console.log(response);
    });

    this.sharedService.getCurrentlySelectedAccounts().subscribe(response => {
      console.log(response);
    });
  }

}
