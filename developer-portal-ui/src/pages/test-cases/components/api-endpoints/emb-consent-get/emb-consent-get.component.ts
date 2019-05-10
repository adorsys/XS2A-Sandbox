import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-consent-get',
  templateUrl: './emb-consent-get.component.html',
  styleUrls: ['./emb-consent-get.component.scss'],
})
export class EmbConsentGetComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
