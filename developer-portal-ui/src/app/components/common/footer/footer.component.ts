import {Component, Input, OnInit} from '@angular/core';
import {NavigationService} from "../../../services/navigation.service";
import {GoogleAnalyticsService} from "../../../services/google-analytics.service";
import {CustomizeService} from "../../../services/customize.service";
import {LanguageService} from "../../../services/language.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  @Input() navigation;
  globalSettings;
  allowedNavigationSize = 5;
  socialMedia = [];
  showNavigation = true;

  private supportedSocialMedia = {
    twitter: "fa-twitter",
    facebook: "fa-facebook-f",
    linkedin: "fa-linkedin-in",
    instagram: "fa-instagram",
    youtube: "fa-youtube",
    xing: "fa-xing"
  };

  private defaultLogoLink = '/home';

  constructor(private navigationService: NavigationService,
              private googleAnalyticsService: GoogleAnalyticsService,
              private customizeService: CustomizeService) {

    this.customizeService.currentTheme
      .subscribe(data => {
        this.globalSettings = data.globalSettings;
        this.allowedNavigationSize = data.pagesSettings.navigationBarSettings.allowedNavigationSize;
      });
  }

  ngOnInit() {
    if (this.allowedNavigationSize && this.navigation && (Object.keys(this.navigation).length > this.allowedNavigationSize)) {
      this.showNavigation = false;
    }

    if (this.globalSettings && this.globalSettings.socialMedia) {
      this.socialMedia = Object.keys(this.globalSettings.socialMedia);
    }
  }

  getIconForSocialMedia(social: any) {
    return `social-media-icon fab ${this.supportedSocialMedia[social]}`;
  }

  sendStats(social: any) {
    this.googleAnalyticsService.eventEmitter(
      social,
      "redirect_to_social_media",
      "click",
      social,
      10
    );
  }
}
