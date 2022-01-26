/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

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
