import {Component, Input, OnInit} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {DataService} from "../../../services/data.service";
import {NavigationService} from "../../../services/navigation.service";
import {CustomizeService} from "../../../services/customize.service";

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  private langCollapsed = false;
  private defaultLogoLink = '/home';

  showNavDropDown = false;
  language = 'en';
  supportedLanguages: string[];

  logo;
  logoLink: string;
  allowedNavigationSize = 5;
  @Input() supportedLanguagesDictionary;
  @Input() navigation;

  constructor(private languageService: LanguageService,
              public dataService: DataService,
              private customizeService: CustomizeService,
              private navigationService: NavigationService) {

    this.customizeService.currentTheme.subscribe(
      data => {
        this.logo = data.globalSettings.logo;
        this.logoLink = data.globalSettings.logoLink;
        this.allowedNavigationSize = data.pagesSettings.navigationBarSettings.allowedNavigationSize;
      });

    this.setLangCollapsed(true);
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
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

  toggleDropdown(e) {
    this.showNavDropDown = !this.showNavDropDown;
  }

  private toggleMenuIfOutOfSize() {
    if (this.navigation.length > this.allowedNavigationSize) {
      document.getElementById('navLinks').style.display = 'none';
      document.getElementById('dropDownIcon').style.display = 'block';
    }
  }
}
