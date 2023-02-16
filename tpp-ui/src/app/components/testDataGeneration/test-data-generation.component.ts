/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { InfoService } from '../../commons/info/info.service';
import { TestDataGenerationService } from '../../services/test.data.generation.service';
import { CurrencyService } from '../../services/currency.service';
import { SpinnerVisibilityService } from 'ng-http-loader';
import { CertificateDownloadService } from '../../services/certificate/certificate-download.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-test-data-generation',
  templateUrl: './test-data-generation.component.html',
  styleUrls: ['./test-data-generation.component.scss'],
})
export class TestDataGenerationComponent implements OnInit, OnDestroy {
  submitted: boolean;
  generatePaymentsFlag: boolean;
  selectedCurrency;
  currencyList;
  certificate;

  private unsubscribe$ = new Subject<void>();

  constructor(
    private generationService: TestDataGenerationService,
    private infoService: InfoService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private currencyService: CurrencyService,
    private certificateDownloadService: CertificateDownloadService,
    private spinner: SpinnerVisibilityService
  ) {}

  ngOnInit(): void {
    this.initializeCurrenciesList();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  generate() {
    return this.generationService
      .generateTestData(this.selectedCurrency, this.generatePaymentsFlag)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(
        (data) => {
          const message =
            'Test data has been successfully generated. The automatic download of the test yml file will start within some seconds.';
          this.infoService.openFeedback(message);

          setTimeout(() => {
            const blob = new Blob([data], { type: 'plain/text' });
            const link = document.createElement('a');
            link.setAttribute('href', window.URL.createObjectURL(blob));
            link.setAttribute('download', 'NISP-Test-Data.yml');
            document.body.appendChild(link);
            link.click();
          }, 3000);
          this.router.navigateByUrl('/accounts');
        },
        (error) => {
          if (error.status === 404) {
            this.infoService.openFeedback("Your Ledgers configuration should have 'develop' profile to activate this feature", {
              severity: 'error',
            });
          } else {
            this.infoService.openFeedback('An Error occurred while generating the Test Data.', { severity: 'error' });
          }
        }
      );
  }

  initializeCurrenciesList() {
    this.spinner.show();

    return this.currencyService
      .getSupportedCurrencies()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(
        (data) => {
          this.currencyList = data;
          this.spinner.hide();
        },
        () => {
          this.infoService.openFeedback('An error occurred while initialize the Currencieslist', { severity: 'error' });
        }
      );
  }

  saveCertificateValue(certificate) {
    this.certificate = certificate;
  }

  generateCertificate() {
    if (this.certificate) {
      const message = 'Certificate was successfully generated. The download will start automatically within the 2 seconds';
      this.certificateDownloadService.generateAndDownloadCertificate(this.certificate, message);
    }
  }
}
