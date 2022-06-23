import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-concent-initation-component',
  templateUrl: './concent-initation.component.html',
  styleUrls: ['./concent-initation.component.scss'],
})
export class ConcentInitationComponent implements OnInit {
  @Input() openConsentMenuPayments: () => void;

  @Input() consentMenu: boolean;

  @Input() openConsentRdctApproach: () => void;

  @Input() consentRdctMenu: boolean;

  @Input() redirectSupported: boolean;

  @Input() openConsentEmbeddedApproach: () => void;

  @Input() embeddedSupported: boolean;

  @Input() consentEmbeddedMenu: boolean;

  constructor() {}

  ngOnInit(): void {}
}
