import { Component, OnInit } from '@angular/core';
import { CustomizeService, Theme } from '../../services/customize.service';
import { saveAs } from 'file-saver';
import { SettingsService } from '../../services/settings.service';
import { Settings } from '../../models/settings.model';

@Component({
  selector: 'app-getting-started',
  templateUrl: './getting-started.component.html',
  styleUrls: ['./getting-started.component.scss'],
})
export class GettingStartedComponent implements OnInit {
  defaultTheme: Theme;
  settings: Settings;

  constructor(
    private customizeService: CustomizeService,
    private settingsService: SettingsService
  ) {}

  exportTheme(): boolean {
    let exportResult = false;
    try {
      const blob = new Blob([JSON.stringify(this.defaultTheme, null, 2)], {
        type: 'application/json',
      });
      saveAs(blob, 'exampleTheme');
      exportResult = true;
    } catch (e) {
      console.log(e);
    }
    return exportResult;
  }

  ngOnInit() {
    this.defaultTheme = this.customizeService.getTheme('default');
    this.settings = this.settingsService.settings;
  }

  getEnvLink(name): string {
    return this.settingsService.getEnvLink(name);
  }
}
