import { Component, OnInit } from '@angular/core';

import { CustomizeService, GlobalSettings, Theme } from './common/services/customize.service';
import { ShareDataService } from './common/services/share-data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  public operation: string;
  public globalSettings: GlobalSettings;

  constructor(private sharedService: ShareDataService,
              private customizeService: CustomizeService) {
    this.sharedService.currentOperation.subscribe(operation => {
      this.operation = operation;
    });
  }

  ngOnInit(): void {
    let theme: Theme;
    this.customizeService.getJSON().subscribe(data => {
      theme = data;
      this.globalSettings = theme.globalSettings;
      if (theme.globalSettings.logo.indexOf('/') === -1) {
        theme.globalSettings.logo =
        '../assets/UI' +
        (this.customizeService.isCustom() ? '/custom/' : '/') +
        theme.globalSettings.logo;
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
      this.customizeService.setUserTheme(theme);
    });
  }

  public checkUrl(): number {
    const url = window.location.href;
    return url.indexOf('/login');
  }

  public checkUrlbank(): number {
    const url = window.location.href;
    return url.indexOf('/bank-offered');
  }
}
