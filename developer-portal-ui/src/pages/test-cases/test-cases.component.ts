import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../services/data.service';
import { CustomizeService } from '../../services/customize.service';
import { AspspService } from '../../services/aspsp.service';

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

  constructor(
    private router: Router,
    public dataService: DataService,
    private actRoute: ActivatedRoute,
    private customizeService: CustomizeService,
    private aspspService: AspspService
  ) {
    this.setUpUsedApproaches();
  }

  onActivate(ev) {
    this.dataService.setRouterUrl(this.actRoute['_routerState'].snapshot.url);
  }

  collapseThis(collapseId: string): void {
    if (
      collapseId === 'redirect' ||
      collapseId === 'embedded' ||
      collapseId === 'account'
    ) {
      const collapsibleItemContent = document.getElementById(
        `${collapseId}-content`
      );

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

      if (collapsibleItemContent.style.maxHeight) {
        collapsibleItemContent.style.maxHeight = '';
      } else {
        collapsibleItemContent.style.maxHeight = 'none';
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
  }

  private setUpUsedApproaches() {
    this.customizeService.getJSON().then(data => {
      const embedded = 'embedded';
      const redirect = 'redirect';

      let redirectSupportedInSettings = data.supportedApproaches.includes(
        redirect
      );
      let embeddedSupportedInSettings = data.supportedApproaches.includes(
        embedded
      );

      this.aspspService.getScaApproaches().subscribe(
        (scaApproaches: Array<string>) => {
          this.redirectSupported =
            redirectSupportedInSettings &&
            scaApproaches.includes(redirect.toLocaleUpperCase());
          this.embeddedSupported =
            embeddedSupportedInSettings &&
            scaApproaches.includes(embedded.toLocaleUpperCase());
        },
        () => {
          this.redirectSupported = redirectSupportedInSettings;
          this.embeddedSupported = embeddedSupportedInSettings;
        }
      );
    });
  }
}
