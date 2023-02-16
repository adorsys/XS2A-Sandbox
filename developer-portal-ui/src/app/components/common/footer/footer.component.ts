/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import { CustomizeService } from '../../../services/customize.service';
import { MarkdownStylingService } from '../../../services/markdown-styling.service';
import { LanguageService } from '../../../services/language.service';
import { UrlService } from 'src/app/services/url.service';
import { EnvLink } from 'src/app/models/envLink.model';
import { MarkdownService } from 'ngx-markdown';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit, AfterViewChecked {
  pathToFile = `./assets/content/i18n/en/footer.md`;
  compiledMarkDown = '';

  constructor(
    private customizeService: CustomizeService,
    private markdownStylingService: MarkdownStylingService,
    private languageService: LanguageService,
    private urlService: UrlService,
    private markdownService: MarkdownService
  ) {}

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/footer.md`;
      this.markdownStylingService.createTableOfContent(true);
      this.markdownService.getSource(this.pathToFile).subscribe((result) => {
        this.compiledMarkDown = this.markdownService.compile(result);
      });
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
