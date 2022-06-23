import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-payment-initation-component',
  templateUrl: './payment-initation.component.html',
  styleUrls: ['./payment-initation.component.scss'],
})
export class PaymentInitationComponent implements OnInit {
  @Input() openSubMenuPayments: () => void;

  @Input() subMenu: boolean;

  @Input() openScaApproach: () => void;

  @Input() redirectSupported: boolean;

  @Input() scaApproach: boolean;

  @Input() openEmbeddedScaApproach: () => void;

  @Input() embeddedSupported: boolean;

  @Input() embeddedScaApproach: boolean;

  @Input() openSubSubMenuPayments: () => void;

  @Input() subSubMenu: boolean;

  constructor() {}

  ngOnInit(): void {}
}
