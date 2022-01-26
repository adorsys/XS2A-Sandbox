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

import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { MarkdownStylingService } from '../../services/markdown-styling.service';
import { LanguageService } from '../../services/language.service';
import { CustomizeService } from '../../services/customize.service';
import { UrlService } from 'src/app/services/url.service';
import { EnvLink } from 'src/app/models/envLink.model';

@Component({
  selector: 'app-getting-started',
  templateUrl: './getting-started.component.html',
  styleUrls: ['./getting-started.component.scss'],
})
export class GettingStartedComponent implements OnInit, AfterViewChecked {
  pathToFile = './assets/content/i18n/en/getting-started.md';

  constructor(
    private markdownStylingService: MarkdownStylingService,
    private languageService: LanguageService,
    private customizeService: CustomizeService,
    private urlService: UrlService
  ) {}

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/getting-started.md`;
      this.markdownStylingService.createTableOfContent(true);
    });
  }

  ngAfterViewChecked() {
    this.urlService.getUrl().subscribe((data: EnvLink) => {
      Object.keys(data.servicesAvailable).forEach((key) => this.setLink(key, data.servicesAvailable[key].environmentLink));
    });
  }

  setLink(id: string, link: string) {
    const anchorNode = document.getElementById(id);
    if (anchorNode) {
      anchorNode.setAttribute('href', link);
    }
  }
}
