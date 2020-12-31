import {Component, OnDestroy, OnInit} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';

import {InfoService} from '../../commons/info/info.service';
import {TestDataGenerationService} from '../../services/test.data.generation.service';
import {CurrencyService} from '../../services/currency.service';
import {SpinnerVisibilityService} from 'ng-http-loader';
import {CertificateDownloadService} from '../../services/certificate/certificate-download.service';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'test-data-generation',
  templateUrl: './test-data-generation.component.html',
  styleUrls: ['./test-data-generation.component.scss']
})
export class TestDataGenerationComponent implements OnInit, OnDestroy {
  submitted: boolean;
  generatePaymentsFlag: boolean;
  selectedCurrency;
  currencyList;
  certificate;

  private unsubscribe$ = new Subject<void>();

  constructor(private generationService: TestDataGenerationService,
              private infoService: InfoService,
              private router: Router,
              private sanitizer: DomSanitizer,
              private currencyService: CurrencyService,
              private certificateDownloadService: CertificateDownloadService,
              private spinner: SpinnerVisibilityService) {
  }

  ngOnInit(): void {
    this.initializeCurrenciesList();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  generate() {
    return this.generationService.generateTestData(this.selectedCurrency, this.generatePaymentsFlag)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(data => {
        const message = 'Test data has been successfully generated. The automatic download of the test yml file will start within some seconds.';
        this.infoService.openFeedback(message);

        setTimeout(() => {
          const blob = new Blob([data], {type: 'plain/text'});
          let link = document.createElement('a');
          link.setAttribute('href', window.URL.createObjectURL(blob));
          link.setAttribute('download', 'NISP-Test-Data.yml');
          document.body.appendChild(link);
          link.click();
        }, 3000);
        this.router.navigateByUrl('/accounts');
      });
  }

  initializeCurrenciesList() {
    this.spinner.show();

    return this.currencyService.getSupportedCurrencies()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(
      data => {
        this.currencyList = data;
        this.spinner.hide();
      },
      () => this.infoService.openFeedback('Currencies list cannot be initialized', {severity: 'error'})
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
