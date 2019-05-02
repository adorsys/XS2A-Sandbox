import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-consent-auth-post',
  templateUrl: './emb-consent-auth-post.component.html',
  styleUrls: ['./emb-consent-auth-post.component.scss'],
})
export class EmbConsentAuthPostComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
