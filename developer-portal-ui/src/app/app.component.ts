import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { LoginService } from '../services/login.service';
import {
  CustomizeService,
  GlobalSettings,
  Theme,
} from '../services/customize.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  globalSettings: GlobalSettings;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public loginService: LoginService,
    public customizeService: CustomizeService
  ) {}

  goToPage(page) {
    this.router.navigateByUrl(`/${page}`);
  }

  onActivate(ev) {
    this.dataService.currentRouteUrl = this.actRoute[
      '_routerState'
    ].snapshot.url;
  }

  ngOnInit() {
    let theme: Theme;
    this.customizeService.getJSON().then(data => {
      theme = data;
      this.customizeService.changeFontFamily(theme.globalSettings.fontFamily);
      this.globalSettings = theme.globalSettings;
      if (theme.globalSettings.logo.indexOf('/') === -1) {
        theme.globalSettings.logo = '../assets/UI/' + theme.globalSettings.logo;
      }
      if (theme.contactInfo.img.indexOf('/') === -1) {
        theme.contactInfo.img = '../assets/UI/' + theme.contactInfo.img;
      }
      this.customizeService.setUserTheme(theme);
    });
  }
}
