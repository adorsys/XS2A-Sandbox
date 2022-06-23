import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-account-information-component',
  templateUrl: './account-information.component.html',
  styleUrls: ['./account-information.component.scss'],
})
export class AccountInformationComponent implements OnInit {
  @Input() openAccountInformation: () => void;

  @Input() accountInformationMenu: boolean;

  constructor() {}

  ngOnInit(): void {}
}
