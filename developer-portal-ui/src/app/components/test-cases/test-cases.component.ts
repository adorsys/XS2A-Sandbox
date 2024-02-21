/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { DataService } from '../../services/data.service';
import { CustomizeService } from '../../services/customize.service';
import { AspspService } from '../../services/aspsp.service';
import { Theme } from '../../models/theme.model';
import { MatAccordion } from '@angular/material/expansion';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styleUrls: ['./test-cases.component.scss'],
})
export class TestCasesComponent implements OnInit {
  isViewInitialized = false;

  redirectSupported = true;
  embeddedSupported = true;
  fundsConfirmationSupported = true;

  subMenu = false;
  subSubMenu = false;
  scaApproach = false;
  embeddedScaApproach = false;
  consentMenu = false;
  consentEmbeddedMenu = false;
  consentRdctMenu = false;
  accountInformationMenu = false;
  fundsConfirmationMenu = false;

  @ViewChild(MatAccordion) accordion: MatAccordion;
  step = 0;

  constructor(
    public dataService: DataService,
    private customizeService: CustomizeService,
    private aspspService: AspspService,
    private router: Router
  ) {
    localStorage.getItem('');
    this.setUpUsedApproaches();
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd && this.isViewInitialized) {
        this.dataService.setRouterUrl(event.urlAfterRedirects);
        this.ngOnInit();
      }
    });
  }

  getMenuStatus() {
    this.subMenu = localStorage.getItem('subMenu') === 'true';
    this.subSubMenu = localStorage.getItem('subSubMenu') === 'true';
    this.scaApproach = localStorage.getItem('scaApproach') === 'true';
    this.embeddedScaApproach = localStorage.getItem('embeddedScaApproach') === 'true';
    this.consentRdctMenu = localStorage.getItem('consentRdctMenu') === 'true';
    this.consentEmbeddedMenu = localStorage.getItem('consentEmbeddedMenu') === 'true';
    this.consentMenu = localStorage.getItem('consentMenu') === 'true';
    this.accountInformationMenu = localStorage.getItem('accountInformationMenu') === 'true';
    this.fundsConfirmationMenu = localStorage.getItem('fundsConfirmationMenu') === 'true';
  }

  ngOnInit() {
    this.isViewInitialized = true;
    this.getMenuStatus();
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

  openSubMenuPayments() {
    this.subMenu = !this.subMenu;
    localStorage.setItem('subMenu', String(this.subMenu));
  }

  openSubSubMenuPayments() {
    this.subSubMenu = !this.subSubMenu;
    localStorage.setItem('subSubMenu', String(this.subSubMenu));
  }

  openScaApproach() {
    this.scaApproach = !this.scaApproach;
    localStorage.setItem('scaApproach', String(this.scaApproach));
  }

  openEmbeddedScaApproach() {
    this.embeddedScaApproach = !this.embeddedScaApproach;
    localStorage.setItem('embeddedScaApproach', String(this.embeddedScaApproach));
  }

  openConsentMenuPayments() {
    this.consentMenu = !this.consentMenu;
    localStorage.setItem('consentMenu', String(this.consentMenu));
  }

  openConsentRdctApproach() {
    this.consentRdctMenu = !this.consentRdctMenu;
    localStorage.setItem('consentRdctMenu', String(this.consentRdctMenu));
  }

  openConsentEmbeddedApproach() {
    this.consentEmbeddedMenu = !this.consentEmbeddedMenu;
    localStorage.setItem('consentEmbeddedMenu', String(this.consentEmbeddedMenu));
  }

  openAccountInformation() {
    this.accountInformationMenu = !this.accountInformationMenu;
    localStorage.setItem('accountInformationMenu', String(this.accountInformationMenu));
  }

  openFundsConfirmation() {
    this.fundsConfirmationMenu = !this.fundsConfirmationMenu;
    localStorage.setItem('fundsConfirmationMenu', String(this.fundsConfirmationMenu));
  }

  closeSidenav() {
    const leftSection = document.getElementById('left-section');
    leftSection.style.display = 'none';
    leftSection.style.width = '0%';

    const rightSection = document.getElementById('right-section');
    rightSection.style.width = '100%';

    const button = document.getElementById('openSideBarButton');
    button.style.display = 'flex';
  }

  openSidenav() {
    const leftSection = document.getElementById('left-section');
    leftSection.style.display = 'block';
    leftSection.style.width = '30%';

    const rightSection = document.getElementById('right-section');
    rightSection.style.width = '70%';

    const button = document.getElementById('openSideBarButton');
    button.style.display = 'none';
  }
}
