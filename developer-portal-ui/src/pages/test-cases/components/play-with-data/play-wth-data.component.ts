import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../../../services/rest.service';
import { DataService } from '../../../../services/data.service';
import { getStatusText } from 'http-status-codes';
import { CopyService } from '../../../../services/copy.service';
import { ConsentTypes } from '../../../../models/consentTypes.model';
import { LocalStorageService } from '../../../../services/local-storage.service';
import { JsonService } from '../../../../services/json.service';

@Component({
  selector: 'app-play-wth-data',
  templateUrl: './play-wth-data.component.html',
  styleUrls: ['./play-wth-data.component.scss'],
})
export class PlayWthDataComponent implements OnInit {
  @Input() method: string;
  @Input() headers: object;
  @Input() body: object;
  @Input() url: string;
  @Input() accountIdFlag: boolean;
  @Input() bookingStatusFlag: boolean;
  @Input() transactionIdFlag: boolean;
  @Input() paymentServiceFlag: boolean;
  @Input() paymentProductFlag: boolean;
  @Input() paymentIdFlag: boolean;
  @Input() cancellationIdFlag: boolean;
  @Input() consentIdFlag: boolean;
  @Input() authorisationIdFlag: boolean;
  @Input() variablePathEnd: string;
  @Input() fieldsToCopy: string[];
  @Input() dateFromFlag: boolean;
  @Input() consentTypeFlag: boolean;
  @Input() consentTypes: ConsentTypes;

  response: HttpResponse<any>;
  finalUrl: string;
  paymentService = '';
  paymentProduct = '';
  @Input() paymentId;
  @Input() cancellationId = '';
  @Input() consentId = '';
  @Input() authorisationId = '';
  @Input() accountId = '';
  @Input() transactionId = '';
  bookingStatus = '';
  redirectUrl = '';
  dateFrom = '';

  paymentServiceSelect = ['payments', 'bulk-payments', 'periodic-payments'];
  paymentProductSelect = [
    'sepa-credit-transfers',
    'instant-sepa-credit-transfers',
    'target-2-payments',
    'cross-border-credit-transfers',
    'pain.001-sepa-credit-transfers',
    'pain.001-instant-sepa-credit-transfers',
    'pain.001-target-2-payments',
    'pain.001-cross-border-credit-transfers',
  ];
  bookingStatusSelect = ['booked', 'pending', 'both'];
  selectedConsentType = 'dedicatedAccountsConsent';

  constructor(
    public restService: RestService,
    public dataService: DataService,
    public copyService: CopyService,
    public localStorageService: LocalStorageService,
    public jsonService: JsonService
  ) {}

  /**
   * Get status text by status code
   * using http-status-codes library
   */
  getStatusText(status) {
    if (status) {
      return getStatusText(status);
    } else {
      return '';
    }
  }

  sendRequest() {
    this.dataService.setIsLoading(true);

    this.finalUrl = this.url;
    if (this.paymentServiceFlag) {
      this.finalUrl += this.paymentService + this.paymentProduct;

      this.finalUrl += this.paymentId ? '/' + this.paymentId : '';
      this.finalUrl += this.variablePathEnd ? this.variablePathEnd : '';
      this.finalUrl += this.authorisationId ? '/' + this.authorisationId : '';
      this.finalUrl += this.cancellationId ? '/' + this.cancellationId : '';
    } else if (this.consentIdFlag) {
      this.finalUrl += '/' + this.consentId;
      this.finalUrl += this.variablePathEnd ? this.variablePathEnd : '';
      this.finalUrl += this.authorisationId ? '/' + this.authorisationId : '';
    } else if (this.accountIdFlag) {
      this.finalUrl += '/' + this.accountId;
      this.finalUrl += this.variablePathEnd ? this.variablePathEnd : '';
      this.finalUrl += this.dateFrom ? '?dateFrom=' + this.dateFrom : '';
      this.finalUrl += this.bookingStatus
        ? '&bookingStatus=' + this.bookingStatus
        : '';
      this.finalUrl += this.transactionId ? '/' + this.transactionId : '';
    }

    console.log('variable', this.variablePathEnd);
    console.log('path: ', this.finalUrl);
    const respBodyEl: any = document.getElementById('textArea');
    if (!respBodyEl || this.isValidJSONString(respBodyEl.value)) {
      const bodyValue = respBodyEl ? JSON.parse(respBodyEl.value) : {};
      this.restService
        .sendRequest(this.method, this.finalUrl, this.headers, bodyValue)
        .subscribe(
          resp => {
            this.response = Object.assign(resp);
            if (
              this.response.body.hasOwnProperty('_links') &&
              this.response.body._links.hasOwnProperty('scaRedirect')
            ) {
              this.redirectUrl = this.response.body._links.scaRedirect.href;
            } else if (this.response.body.hasOwnProperty('paymentId')) {
              this.paymentId = this.response.body.paymentId;
              this.localStorageService.set(
                'paymentId',
                this.response.body.paymentId
              );
            } else if (this.response.body.hasOwnProperty('authorisationId')) {
              this.authorisationId = this.response.body.authorisationId;
              this.localStorageService.set(
                'authorisationId',
                this.authorisationId
              );
            } else if (this.response.body.hasOwnProperty('consentId')) {
              this.consentId = this.response.body.consentId;
              this.localStorageService.set('consentId', this.consentId);
            } else if (this.response.body.hasOwnProperty('cancellationId')) {
              this.cancellationId = this.response.body.cancellationId;
              this.localStorageService.set(
                'cancellationId',
                this.cancellationId
              );
            } else if (this.response.body.hasOwnProperty('accountId')) {
              this.accountId = this.response.body.accountId;
              this.localStorageService.set('accountId', this.accountId);
            } else if (this.response.body.hasOwnProperty('transactionId')) {
              this.transactionId = this.response.body.transactionId;
              this.localStorageService.set('transactionId', this.transactionId);
            }
            this.dataService.setIsLoading(false);
            this.dataService.showToast('Request sent', 'Success!', 'success');
          },
          err => {
            this.dataService.setIsLoading(false);
            this.dataService.showToast(
              'Something went wrong!',
              'Error!',
              'error'
            );
            this.response = Object.assign(err);
            console.log('err', JSON.stringify(err));
          }
        );
    } else {
      this.dataService.setIsLoading(false);
      this.dataService.showToast('Body in not valid!', 'Error!', 'error');
    }
  }

  // Check if text in body in JSON format
  isValidJSONString(str) {
    try {
      JSON.parse(str);
    } catch (e) {
      return false;
    }
    return true;
  }

  // Fixing the loss of input focus
  trackByFn(index: any, item: any) {
    return index;
  }

  handleConsentSelected(consentType: string) {
    this.body = this.consentTypes[consentType];
  }

  ngOnInit() {
    this.paymentService = this.paymentServiceFlag ? 'payments' : '';
    this.paymentProduct = this.paymentProductFlag
      ? '/sepa-credit-transfers'
      : '';
    this.paymentId = this.paymentIdFlag ? this.paymentId : '';
    this.cancellationId = this.cancellationIdFlag ? this.cancellationId : '';
    this.consentId = this.consentIdFlag ? this.consentId : '';
    this.authorisationId = this.authorisationIdFlag ? this.authorisationId : '';
    this.accountId = this.accountIdFlag ? this.accountId : '';
    this.transactionId = this.transactionIdFlag ? this.transactionId : '';
    this.bookingStatus = this.bookingStatusFlag ? 'booked' : '';
    this.dateFrom = this.dateFromFlag ? '2010-10-10' : '';
    this.fieldsToCopy = this.fieldsToCopy ? this.fieldsToCopy : [];
  }

  handlePaymentTypeChanged(
    paymentType: string,
    paymentProductChanged: boolean
  ) {
    const paymentService = paymentProductChanged
      ? this.paymentService
      : paymentType;

    const paymentProduct = paymentProductChanged
      ? paymentType
      : this.paymentProduct;

    this.jsonService
      .getPreparedJsonData(paymentService + '/' + paymentProduct + '.json')
      .subscribe(data => (this.body = data));
  }
}
