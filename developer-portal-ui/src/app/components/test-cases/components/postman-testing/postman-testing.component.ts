import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../services/language.service';
import { GoogleAnalyticsService } from '../../../../services/google-analytics.service';
import { CustomizeService } from '../../../../services/customize.service';
import { EVENT_VALUE } from '../../../common/constant/constants';

@Component({
  selector: 'app-postman-testing',
  templateUrl: './postman-testing.component.html',
  styleUrls: ['./postman-testing.component.scss'],
})
export class PostmanTestingComponent implements OnInit {
  pathToPostman = `./assets/content/i18n/en/test-cases/postman.md`;

  constructor(
    private languageService: LanguageService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private customizeService: CustomizeService
  ) {}

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToPostman = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/postman.md`;
    });
  }

  sendPostmanStats() {
    if (this.googleAnalyticsService.enabled) {
      this.googleAnalyticsService.eventEmitter('download_postman', 'download', 'click', 'postman', EVENT_VALUE);
    }
  }
}
