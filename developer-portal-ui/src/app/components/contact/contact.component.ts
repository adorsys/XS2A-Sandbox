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

import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';
import { ContactInfo, OfficeInfo, Theme } from '../../models/theme.model';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent implements OnInit {
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];

  pathToFile = `./assets/content/i18n/en/contact.md`;

  constructor(public customizeService: CustomizeService, private languageService: LanguageService) {
    if (this.customizeService.currentTheme) {
      this.customizeService.currentTheme.subscribe((theme: Theme) => {
        if (theme.pagesSettings) {
          const contactPageSettings = theme.pagesSettings.contactPageSettings;
          if (contactPageSettings) {
            this.setContactInfo(contactPageSettings.contactInfo);
            this.setOfficesInfo(contactPageSettings.officesInfo);
          }
        }
      });
    }
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/contact.md`;
    });
  }

  private setContactInfo(contactInfo: ContactInfo) {
    if (contactInfo) {
      this.contactInfo = contactInfo;
    }
  }

  private setOfficesInfo(officesInfo: OfficeInfo[]) {
    if (officesInfo) {
      this.officesInfo = officesInfo;
    }
  }
}
