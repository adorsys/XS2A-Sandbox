import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {HttpClient} from "@angular/common/http";
import {GlobalSettings} from "./models/theme.model";
import {DataService} from "./services/data.service";
import {CustomizeService} from "./services/customize.service";
import {LanguageService} from "./services/language.service";
import {filter} from "rxjs/operators";
import {MarkdownStylingService} from "./services/markdown-styling.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  globalSettings: GlobalSettings;
  supportedLanguagesDictionary;
  navigation;
  allowedNavigationSize;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public customizeService: CustomizeService,
    private languageService: LanguageService,
    private http: HttpClient,
    private markdownStylingService: MarkdownStylingService) {

    this.customizeService.getJSON().then(data => {
      this.supportedLanguagesDictionary = data.supportedLanguagesDictionary;

      localStorage.setItem('tppDefaultNokRedirectUrl', data.tppSettings.tppDefaultNokRedirectUrl);
      localStorage.setItem('tppDefaultRedirectUrl', data.tppSettings.tppDefaultRedirectUrl);

      this.setUpRoutes(data); // TODO make it in customize Service https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/591
      this.allowedNavigationSize = data.pagesSettings.navigationBarSettings.allowedNavigationSize;
    });

    this.languageService.initializeTranslation();
  }

  onActivate(ev) {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.http.get(`assets/i18n/${data}/navigation.json`).subscribe(
          data => this.navigation = data['navigation']
        );
      });

    this.adjustMarkdownViews();
  }

  private setUpRoutes(data) {
    let theme = data;
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
  }

  private adjustMarkdownViews() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)).subscribe((event: NavigationEnd) => {
      this.markdownStylingService.resetCounter();

      document.getElementById("spacer").style.display = "block";
      if (event.url === '/home' || event.url === '/') {
        document.getElementById("home-spacer").style.display = "block";
      }

      setTimeout(() => {
        document.getElementById("spacer").style.display = "none";
        if (event.url === '/home' || event.url === '/') {
          document.getElementById("home-spacer").style.display = "none";
        }
      }, 500)
    });
  }
}
