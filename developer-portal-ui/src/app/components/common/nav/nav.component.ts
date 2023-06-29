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

import { Component, Input, OnInit } from '@angular/core';
import { LanguageService } from '../../../services/language.service';
import { DataService } from '../../../services/data.service';
import { CustomizeService } from '../../../services/customize.service';
import { NavigationService } from '../../../services/navigation.service';
import { NavigationSettings, Theme } from '../../../models/theme.model';
import { NavItem } from 'src/app/models/navItem.model';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss'],
})
export class NavComponent implements OnInit {
  private langCollapsed = false;

  showNavDropDown = false;
  language = 'en';
  supportedLanguages: string[];

  navBarSettings: NavigationSettings;
  @Input() supportedLanguagesDictionary;
  @Input() navigation;
  menu: any;

  constructor(
    private languageService: LanguageService,
    public dataService: DataService,
    private customizeService: CustomizeService,
    public navigationService: NavigationService
  ) {
    this.customizeService.currentTheme.subscribe((data: Theme) => {
      if (data.globalSettings.logo) {
        this.navBarSettings = {logo: data.globalSettings.logo}
      }
    });
    
    this.setLangCollapsed(true);
  }
  
  
  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.language = data;
    });

    if (this.supportedLanguagesDictionary) {
      this.supportedLanguages = Object.keys(this.supportedLanguagesDictionary);
    }

    if (this.navigation) {
      this.toggleMenuIfOutOfSize();
    }
  }

  changeLang(language: string) {
    this.language = language;
    this.languageService.setLanguage(language);
    this.collapseThis();
  }

  setLangCollapsed(value: boolean) {
    this.langCollapsed = value;
  }

  collapseThis() {
    if (this.supportedLanguages && this.supportedLanguages.length > 1) {
      this.setLangCollapsed(!this.getLangCollapsed());
    }
  }

  getLangCollapsed() {
    return this.langCollapsed;
  }

  toggleDropdown() {
    this.showNavDropDown = !this.showNavDropDown;
  }

  private toggleMenuIfOutOfSize() {
    if (
      this.navBarSettings &&
      this.navBarSettings.allowedNavigationSize &&
      this.navigation.length > this.navBarSettings.allowedNavigationSize
    ) {
      document.getElementById('navLinks').style.display = 'none';
      document.getElementById('dropDownIcon').style.display = 'block';
    }
  }

  createRouterLinks(navItem: NavItem): string[] {
    if (navItem.type === 'markdown') {
      return ['/page/faq'];
    }
    return [navItem.route];
  }
}
