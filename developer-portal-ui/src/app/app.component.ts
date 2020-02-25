import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { CustomizeService } from '../services/customize.service';
import { TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../services/language.service';
import { GlobalSettings, Theme } from '../models/theme.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  globalSettings: GlobalSettings;
  lang = 'en';
  langs: string[];
  langIcons: object = {
    en: '../assets/icons/united-kingdom.png',
    de: '../assets/icons/germany.png',
    es: '../assets/icons/spain.png',
    ua: '../assets/icons/ukraine.png',
  };
  private langCollapsed = false;
  public showNavDropDown = false;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public customizeService: CustomizeService,
    private translateService: TranslateService,
    private languageService: LanguageService
  ) {
    this.customizeService.getJSON().then(data => {
      this.langs = data.supportedLanguages;
      localStorage.setItem(
        'tppDefaultNokRedirectUrl',
        data.tppSettings.tppDefaultNokRedirectUrl
      );
      localStorage.setItem(
        'tppDefaultRedirectUrl',
        data.tppSettings.tppDefaultRedirectUrl
      );
    });
    this.languageService.initializeTranslation();
    this.setLangCollapsed(true);
  }

  goToPage(page) {
    this.router.navigateByUrl(`/${page}`);
  }

  onActivate(ev) {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  changeLang(lang: string) {
    this.lang = lang;
    this.languageService.setLang(lang);
    this.collapseThis();
  }

  setLangCollapsed(value: boolean) {
    this.langCollapsed = value;
  }

  collapseThis() {
    if (this.langs && this.langs.length > 1) {
      this.setLangCollapsed(!this.getLangCollapsed());
    }
  }

  getLangCollapsed() {
    return this.langCollapsed;
  }

  toggleDropdown(e) {
    this.showNavDropDown = !this.showNavDropDown;
  }

  ngOnInit() {
    let theme: Theme;
    this.customizeService.getJSON().then(data => {
      theme = data;
      this.globalSettings = theme.globalSettings;
      if (theme.globalSettings.logo.indexOf('/') === -1) {
        theme.globalSettings.logo =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.globalSettings.logo;
      }
      if (theme.globalSettings.footerLogo.indexOf('/') === -1) {
        theme.globalSettings.footerLogo =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.globalSettings.footerLogo;
      }
      if (
        theme.globalSettings.favicon &&
        theme.globalSettings.favicon.href.indexOf('/') === -1
      ) {
        theme.globalSettings.favicon.href =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.globalSettings.favicon.href;
      }
      if (theme.contactInfo.img.indexOf('/') === -1) {
        theme.contactInfo.img =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.contactInfo.img;
      }
      this.customizeService.setUserTheme(theme);
    });
  }
}
