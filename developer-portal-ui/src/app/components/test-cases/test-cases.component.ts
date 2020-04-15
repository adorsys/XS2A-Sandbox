import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DataService } from '../../services/data.service';
import { CustomizeService } from '../../services/customize.service';
import { AspspService } from '../../services/aspsp.service';
import { LanguageService } from '../../services/language.service';
import { Theme } from '../../models/theme.model';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styleUrls: ['./test-cases.component.scss'],
})
export class TestCasesComponent implements OnInit {
  redirectFlag = false;
  embeddedFlag = false;
  accountFlag = false;
  redirectSupported = true;
  embeddedSupported = true;

  pathToHeadTestCases = `./assets/content/i18n/en/test-cases/headTestCases.md`;

  constructor(
    public dataService: DataService,
    private actRoute: ActivatedRoute,
    private customizeService: CustomizeService,
    private aspspService: AspspService,
    private languageService: LanguageService
  ) {
    this.setUpUsedApproaches();
  }

  onActivate() {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  collapseThis(collapseId: string): void {
    if (collapseId === 'redirect' || collapseId === 'embedded' || collapseId === 'account') {
      const collapsibleItemContent = document.getElementById(`${collapseId}-content`);

      switch (collapseId) {
        case 'redirect':
          this.redirectFlag = !this.redirectFlag;
          break;
        case 'embedded':
          this.embeddedFlag = !this.embeddedFlag;
          break;
        case 'account':
          this.accountFlag = !this.accountFlag;
          break;
      }

      if (collapsibleItemContent) {
        if (collapsibleItemContent.style.maxHeight) {
          collapsibleItemContent.style.maxHeight = '';
        } else {
          collapsibleItemContent.style.maxHeight = 'none';
        }
      }
    }
  }

  ngOnInit() {
    if (this.dataService.getRouterUrl().includes('account')) {
      this.collapseThis('account');
    } else if (this.dataService.getRouterUrl().includes('embedded')) {
      this.collapseThis('embedded');
    } else if (this.dataService.getRouterUrl().includes('redirect')) {
      this.collapseThis('redirect');
    }

    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToHeadTestCases = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/headTestCases.md`;
    });
  }

  private setUpUsedApproaches() {
    this.customizeService.currentTheme.subscribe((data: Theme) => {
      if (data.pagesSettings) {
        const playWithDataSettings = data.pagesSettings.playWithDataSettings;
        if (playWithDataSettings && playWithDataSettings.supportedApproaches) {
          const embedded = 'embedded';
          const redirect = 'redirect';

          const redirectSupportedInSettings = playWithDataSettings.supportedApproaches.includes(redirect);
          const embeddedSupportedInSettings = playWithDataSettings.supportedApproaches.includes(embedded);

          this.aspspService.getScaApproaches().subscribe(
            (scaApproaches: Array<string>) => {
              this.redirectSupported = redirectSupportedInSettings && scaApproaches.includes(redirect.toLocaleUpperCase());
              this.embeddedSupported = embeddedSupportedInSettings && scaApproaches.includes(embedded.toLocaleUpperCase());
            },
            () => {
              this.redirectSupported = redirectSupportedInSettings;
              this.embeddedSupported = embeddedSupportedInSettings;
            }
          );
        }
      }
    });
  }
}
