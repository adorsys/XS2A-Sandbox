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

import { Component, OnInit, AfterViewChecked, Input } from '@angular/core';
import { CustomizeService } from '../../../services/customize.service';
import { LanguageService } from '../../../services/language.service';
import { UrlService } from 'src/app/services/url.service';
import { EnvLink } from 'src/app/models/envLink.model';
import { NavigationSettings, Theme } from 'src/app/models/theme.model';
import { NavigationService } from 'src/app/services/navigation.service';
import { NavItem } from 'src/app/models/navItem.model';
import { SocialMedia } from './../../../models/theme.model';


@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit, AfterViewChecked {
  language = 'en';
  supportedLanguages: string[];
  socialMedia: SocialMedia;
  
  navBarSettings: NavigationSettings;
  @Input() supportedLanguagesDictionary;
  @Input() navigation;


  constructor(
    private customizeService: CustomizeService,
    private languageService: LanguageService,
    private urlService: UrlService,
    public navigationService: NavigationService

  ) {    this.customizeService.currentTheme.subscribe((data: Theme) => {
    if (data.globalSettings.logo) {
      this.navBarSettings = {logo: data.globalSettings.logo}
    };
    if (data.pagesSettings) {
      const footerSettings = data.pagesSettings.footerSettings;
      if (footerSettings) {
        this.setSocialMediaSettings(footerSettings.socialMedia);
      }
    }
  });}

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

  setSocialMediaSettings(socialMedia: SocialMedia) {
    if (socialMedia) {
      this.socialMedia = socialMedia
    }
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
