import { Component, Input, OnInit } from '@angular/core';

@Component({
  standalone: false,
  selector: 'app-verification-of-pay-nav',
  templateUrl: './verification-of-pay-nav.component.html',
  styleUrls: ['./verification-of-pay-nav.component.scss'],
})
export class VerificationOfPayNavComponent implements OnInit {
  @Input() openVerificationOfPay: () => void;

  @Input() verificationOfPayMenu: boolean;

  @Input() openSingleVopApproach: () => void;

  @Input() singleVopApproach: boolean;

  @Input() openBulkVopApproach: () => void;

  @Input() bulkVopApproach: boolean;

  constructor() {}

  ngOnInit(): void {}
}
