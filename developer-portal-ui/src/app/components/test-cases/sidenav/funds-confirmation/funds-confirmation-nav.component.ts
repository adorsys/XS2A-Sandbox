import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-funds-confirmation-nav',
  templateUrl: './funds-confirmation-nav.component.html',
  styleUrls: ['./funds-confirmation-nav.component.scss'],
})
export class FundsConfirmationNavComponent implements OnInit {
  @Input() openFundsConfirmation: () => void;

  @Input() fundsConfirmationMenu: boolean;

  constructor() {}

  ngOnInit(): void {}
}
