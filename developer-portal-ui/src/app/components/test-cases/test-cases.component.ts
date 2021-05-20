import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { DataService } from '../../services/data.service';
import { CustomizeService } from '../../services/customize.service';
import { AspspService } from '../../services/aspsp.service';
import { LanguageService } from '../../services/language.service';
import { Theme } from '../../models/theme.model';
import { MatAccordion } from '@angular/material/expansion';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styleUrls: ['./test-cases.component.scss'],
})
export class TestCasesComponent implements OnInit {
  redirectFlag = false;
  embeddedFlag = false;
  accountFlag = false;
  fundsConfirmationFlag = false;
  isViewInitialized = false;

  redirectSupported = true;
  embeddedSupported = true;
  fundsConfirmationSupported = true;

  pathToHeadTestCases = `./assets/content/i18n/en/test-cases/headTestCases.md`;
  @ViewChild(MatAccordion) accordion: MatAccordion;
  panelOpenState = false;
  step = 0;

  constructor(
    public dataService: DataService,
    private customizeService: CustomizeService,
    private aspspService: AspspService,
    private languageService: LanguageService,
    private router: Router
  ) {
    this.setUpUsedApproaches();
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd && this.isViewInitialized) {
        this.dataService.setRouterUrl(event.urlAfterRedirects);
        this.ngOnInit();
      }
    });
  }

  onActivate() {}

  collapseThis(collapseId: string): void {
    if (collapseId === 'redirect' || collapseId === 'embedded' || collapseId === 'account' || collapseId === 'funds-confirmation') {
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
        case 'funds-confirmation':
          this.fundsConfirmationFlag = !this.fundsConfirmationFlag;
          break;
      }
    }
  }

  ngOnInit() {
    this.isViewInitialized = true;
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
        this.fundsConfirmationSupported = playWithDataSettings.fundConfirmationSupported;

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
  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
}
