import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { DataService } from './services/data.service';
import { LanguageService } from './services/language.service';
import { filter } from 'rxjs/operators';
import { MarkdownStylingService } from './services/markdown-styling.service';
import { GoogleAnalyticsService } from './services/google-analytics.service';
import { CustomizeService } from './services/customize.service';
import { PagesSettings, Theme } from './models/theme.model';
import { NavigationService } from './services/navigation.service';
import { CertificateService } from './services/certificate.service';
import { LocalStorageService } from './services/local-storage.service';
import { TPP_NOK_REDIRECT_URL_KEY, TPP_REDIRECT_URL_KEY } from './components/common/constant/constants';

declare let gtag;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  private adjustMarkdownTime = 500;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public customizeService: CustomizeService,
    private languageService: LanguageService,
    private markdownStylingService: MarkdownStylingService,
    private certificateService: CertificateService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private navigationService: NavigationService
  ) {
    this.customizeService.currentTheme.subscribe((theme: Theme) => {
      this.setUpSettings(theme);
      this.customizeService
        .normalizeLanguages(theme)
        .then(
          (normalizedTheme: Theme) => (this.supportedLanguagesDictionary = normalizedTheme.globalSettings.supportedLanguagesDictionary)
        );
    });
    this.setUpCertificate();
    this.languageService.initializeTranslation();
  }

  supportedLanguagesDictionary;
  navigation;

  private static createScripts(googleAnalyticsCode: string) {
    const gaScript = document.createElement('script');
    gaScript.setAttribute('async', 'true');
    gaScript.setAttribute('src', `https://www.googletagmanager.com/gtag/js?id=${googleAnalyticsCode}`);

    const gaScript2 = document.createElement('script');
    gaScript2.innerText = `window.dataLayer = window.dataLayer || [];function gtag(){dataLayer.push(arguments);}gtag(\'js\', new Date());gtag(\'config\', \'${googleAnalyticsCode}\');`;

    document.documentElement.firstChild.appendChild(gaScript);
    document.documentElement.firstChild.appendChild(gaScript2);
  }

  private setUpSettings(theme: Theme) {
    this.setUpTppSettings(theme.pagesSettings);

    if (theme.globalSettings) {
      this.customizeService.setStyling(theme);
      this.setUpGoogleAnalytics(theme.globalSettings.googleAnalyticsTrackingId);
    }
  }

  onActivate() {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.navigationService.getNavigation(`${this.customizeService.currentLanguageFolder}/${data}`).then((nav) => (this.navigation = nav));
    });

    this.adjustMarkdownViews();
  }

  private adjustMarkdownViews() {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((event: NavigationEnd) => {
      this.markdownStylingService.resetCounter();

      document.getElementById('spacer').style.display = 'block';
      if (event.url === '/home' || event.url === '/') {
        document.getElementById('home-spacer').style.display = 'block';
      }

      setTimeout(() => {
        document.getElementById('spacer').style.display = 'none';
        if (event.url === '/home' || event.url === '/') {
          document.getElementById('home-spacer').style.display = 'none';
        }
      }, this.adjustMarkdownTime);
    });
  }

  private setUpGoogleAnalytics(googleAnalyticsCode: string) {
    if (googleAnalyticsCode && googleAnalyticsCode !== '') {
      this.googleAnalyticsService.enabled = true;
      AppComponent.createScripts(googleAnalyticsCode);
      this.setUpGoogleAnlyticsPageViews(googleAnalyticsCode);
    }
  }

  private setUpGoogleAnlyticsPageViews(googleAnalyticsCode: string) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        gtag('config', googleAnalyticsCode, {
          page_path: event.urlAfterRedirects,
        });
      }
    });
  }

  private setUpCertificate() {
    this.certificateService
      .getQwacCertificate()
      .toPromise()
      .then((data) => this.certificateService.storeCertificate(data));
  }

  private setUpTppSettings(pagesSettings: PagesSettings) {
    if (pagesSettings) {
      const playWithDataSettings = pagesSettings.playWithDataSettings;

      if (playWithDataSettings && playWithDataSettings.tppSettings) {
        LocalStorageService.set(TPP_NOK_REDIRECT_URL_KEY, playWithDataSettings.tppSettings.tppDefaultNokRedirectUrl);
        LocalStorageService.set(TPP_REDIRECT_URL_KEY, playWithDataSettings.tppSettings.tppDefaultRedirectUrl);
      }
    }
  }
}
