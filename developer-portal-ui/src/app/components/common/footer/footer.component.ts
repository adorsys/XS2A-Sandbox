import { Component, Input, OnInit } from '@angular/core';
import { GoogleAnalyticsService } from '../../../services/google-analytics.service';
import { CustomizeService } from '../../../services/customize.service';
import { NavigationSettings, Theme } from '../../../models/theme.model';
import { NavigationService } from '../../../services/navigation.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  @Input() navigation;
  footerSettings: NavigationSettings;
  socialMedia = [];
  socialMediaDictionary: object;
  showNavigation = true;

  constructor(
    public googleAnalyticsService: GoogleAnalyticsService,
    private customizeService: CustomizeService,
    public navigationService: NavigationService
  ) {
    this.customizeService.currentTheme.subscribe((theme: Theme) => {
      if (theme.pagesSettings && theme.pagesSettings.footerSettings) {
        this.footerSettings = theme.pagesSettings.footerSettings;
      }

      if (theme.globalSettings && theme.globalSettings.socialMedia) {
        this.socialMediaDictionary = theme.globalSettings.socialMedia;
        this.socialMedia = Object.keys(this.socialMediaDictionary);
      }
    });
  }

  ngOnInit() {
    if (
      this.footerSettings &&
      this.footerSettings.allowedNavigationSize &&
      this.navigation &&
      Object.keys(this.navigation).length > this.footerSettings.allowedNavigationSize
    ) {
      this.showNavigation = false;
    }
  }
}
