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

import { CustomizeService, Theme, GlobalSettings } from './services/customize.service';
import { Title } from '@angular/platform-browser';
import { CountryService } from './services/country.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'app';
  globalSettings: GlobalSettings;

  constructor(private customizeService: CustomizeService, private countryService: CountryService, private titleService: Title) {}

  ngOnInit() {
    this.countryService.loadCountries();
    let theme: Theme;
    this.customizeService.getJSON().subscribe((data) => {
      theme = data;
      this.globalSettings = theme.globalSettings;
      if (theme.globalSettings.logo.indexOf('/') === -1) {
        theme.globalSettings.logo = '../assets/UI' + (this.customizeService.isCustom() ? '/custom/' : '/') + theme.globalSettings.logo;
      }
      if (theme.globalSettings.favicon && theme.globalSettings.favicon.href.indexOf('/') === -1) {
        theme.globalSettings.favicon.href =
          '../assets/UI' + (this.customizeService.isCustom() ? '/custom/' : '/') + theme.globalSettings.favicon.href;
      }

      const title = theme.globalSettings.title;
      if (title) {
        this.titleService.setTitle(title);
      }
      this.customizeService.setUserTheme(theme);
    });
  }
}
