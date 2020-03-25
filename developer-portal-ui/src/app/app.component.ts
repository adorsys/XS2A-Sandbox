import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {HttpClient} from "@angular/common/http";
import {DataService} from "./services/data.service";
import {LanguageService} from "./services/language.service";
import {filter} from "rxjs/operators";
import {MarkdownStylingService} from "./services/markdown-styling.service";
import {TrackingIdService} from "./services/tracking-id.service";
import {GoogleAnalyticsService} from "./services/google-analytics.service";
import {CustomizeService} from "./services/customize.service";
import {Theme} from "./models/theme.model";
import {NavigationService} from "./services/navigation.service";

declare let gtag: Function;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  supportedLanguagesDictionary;
  navigation;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public customizeService: CustomizeService,
    private languageService: LanguageService,
    private http: HttpClient,
    private markdownStylingService: MarkdownStylingService,
    private trackingIdService: TrackingIdService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private navigationService: NavigationService) {

    this.setUpGoogleAnalytics(trackingIdService.trackingId[0].trackingId);

    this.customizeService.currentTheme
      .subscribe(theme => {
        this.customizeService.setStyling(theme);
        this.customizeService.normalizeLanguages(theme)
          .then((theme: Theme) => this.supportedLanguagesDictionary = theme.supportedLanguagesDictionary);

        localStorage.setItem('tppDefaultNokRedirectUrl', theme.tppSettings.tppDefaultNokRedirectUrl);
        localStorage.setItem('tppDefaultRedirectUrl', theme.tppSettings.tppDefaultRedirectUrl);
      });

    this.languageService.initializeTranslation();
  }

  onActivate(ev) {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.navigationService.getNavigation(`${this.customizeService.currentLanguageFolder}/${data}`)
          .then(nav => this.navigation = nav);
      });

    this.adjustMarkdownViews();
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

  private setUpGoogleAnalytics(googleAnalyticsCode: string) {
    if (googleAnalyticsCode && googleAnalyticsCode !== '') {
      this.googleAnalyticsService.enabled = true;
      AppComponent.createScripts(googleAnalyticsCode);
      this.setUpGoogleAnlyticsPageViews(googleAnalyticsCode);
    }
  }

  private static createScripts(googleAnalyticsCode: string) {
    let gaScript = document.createElement('script');
    gaScript.setAttribute('async', 'true');
    gaScript.setAttribute('src', `https://www.googletagmanager.com/gtag/js?id=${googleAnalyticsCode}`);

    let gaScript2 = document.createElement('script');
    gaScript2.innerText = `window.dataLayer = window.dataLayer || [];function gtag(){dataLayer.push(arguments);}gtag(\'js\', new Date());gtag(\'config\', \'${googleAnalyticsCode}\');`;

    document.documentElement.firstChild.appendChild(gaScript);
    document.documentElement.firstChild.appendChild(gaScript2);
  }

  private setUpGoogleAnlyticsPageViews(googleAnalyticsCode: string) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        gtag('config', googleAnalyticsCode,
          {
            'page_path': event.urlAfterRedirects
          }
        );
      }
    });
  }
}
