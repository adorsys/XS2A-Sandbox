import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../../services/language.service';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-emb-consent-create-post',
  templateUrl: './emb-consent-create-post.component.html',
})
export class EmbConsentCreatePostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData: object;
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'TPP-Redirect-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };
  body: object;

  constructor(
    public languageService: LanguageService,
    private jsonService: JsonService
  ) {
    jsonService
      .getJsonData(jsonService.jsonLinks.consent)
      .subscribe(data => (this.jsonData = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.consent)
      .subscribe(data => (this.body = data), error => console.log(error));
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
