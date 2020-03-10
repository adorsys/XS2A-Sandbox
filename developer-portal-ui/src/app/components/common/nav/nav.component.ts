import {Component, Input, OnInit} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {DataService} from "../../../services/data.service";

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  private langCollapsed = false;

  showNavDropDown = false;
  language = 'en';
  supportedLanguages: string[];

  @Input() navigation;
  @Input() logo;
  @Input() supportedLanguagesDictionary;
  @Input() allowedNavigationSize = 3;

  constructor(private languageService: LanguageService,
              public dataService: DataService) {
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
