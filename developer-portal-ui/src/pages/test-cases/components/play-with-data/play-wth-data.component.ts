import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../../../services/rest.service';
import { DataService } from '../../../../services/data.service';
import { getStatusText } from 'http-status-codes';
import { CopyService } from '../../../../services/copy.service';
import { ConsentTypes } from '../../../../models/consentTypes.model';
import { LocalStorageService } from '../../../../services/local-storage.service';
import { JsonService } from '../../../../services/json.service';
import * as vkbeautify from 'vkbeautify';
import { AspspService } from '../../../../services/aspsp.service';
import { PaymentTypesMatrix } from '../../../../models/paymentTypesMatrix.model';

@Component({
  selector: 'app-play-wth-data',
  templateUrl: './play-wth-data.component.html',
  styleUrls: ['./play-wth-data.component.scss'],
})
export class PlayWthDataComponent implements OnInit {
  @Input() method: string;
  @Input() headers: object;
  @Input() body;
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
  xml = false;

  paymentServiceSelect = [];
  paymentProductSelect = [];
  bookingStatusSelect = [];
  selectedConsentType = 'dedicatedAccountsConsent';

  paymentTypesMatrix: PaymentTypesMatrix;
  paymentTypes = ['payments', 'periodic-payments', 'bulk-payments'];

  constructor(
    public restService: RestService,
    public dataService: DataService,
    public copyService: CopyService,
    public localStorageService: LocalStorageService,
    public jsonService: JsonService,
    public aspspService: AspspService
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

    const respBodyEl: any = this.xml
      ? document.getElementById('textAreaXml')
      : document.getElementById('textArea');
    const requestBody = respBodyEl ? respBodyEl.value.toString() : {};

    this.restService
      .sendRequest(
        this.method,
        this.finalUrl,
        this.headers,
        this.xml,
        requestBody
      )
      .subscribe(
        resp => {
          delete this.headers['Content-Type'];
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
            this.localStorageService.set('cancellationId', this.cancellationId);
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
          delete this.headers['Content-Type'];
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
  }

  // Fixing the loss of input focus
  trackByFn(index: any, item: any) {
    return index;
  }

  handleConsentSelected(consentType: string) {
    this.body = this.consentTypes[consentType];
  }

  ngOnInit() {
    this.aspspService.getAspspProfile().subscribe(data => {
      if (data.pis.supportedPaymentTypeAndProductMatrix) {
        this.paymentTypesMatrix = data.pis.supportedPaymentTypeAndProductMatrix;
        this.setPaymentServicesAndProducts();
        this.setBookingStatuses(
          data.ais.transactionParameters.availableBookingStatuses
        );
        this.setDefaultFields();
      }
    });
  }

  private setPaymentServicesAndProducts() {
    for (const paymentType of this.paymentTypes) {
      const matrixElement = this.paymentTypesMatrix[paymentType];

      if (matrixElement && matrixElement.length > 0) {
        this.paymentServiceSelect.push(paymentType);

        if (this.paymentService === '') {
          this.paymentService = paymentType;

          if (this.paymentProductFlag) {
            this.paymentProductSelect = matrixElement;
            this.paymentProduct = '/' + this.paymentProductSelect[0];
          }
        }
      }
    }
  }

  private setBookingStatuses(bookingStatuses?: Array<string>) {
    if (bookingStatuses) {
      this.bookingStatus =
        this.bookingStatusFlag && bookingStatuses.length > 0
          ? bookingStatuses[0]
          : '';
      this.bookingStatusSelect = bookingStatuses;
    }
  }

  private setDefaultFields() {
    this.paymentId = this.paymentIdFlag ? this.paymentId : '';
    this.cancellationId = this.cancellationIdFlag ? this.cancellationId : '';
    this.consentId = this.consentIdFlag ? this.consentId : '';
    this.authorisationId = this.authorisationIdFlag ? this.authorisationId : '';
    this.accountId = this.accountIdFlag ? this.accountId : '';
    this.transactionId = this.transactionIdFlag ? this.transactionId : '';

    this.dateFrom = this.dateFromFlag
      ? new Date().toISOString().slice(0, 10)
      : '';
    this.fieldsToCopy = this.fieldsToCopy ? this.fieldsToCopy : [];
  }

  public handlePaymentServiceChanged(paymentService: string) {
    this.paymentProductSelect = this.paymentTypesMatrix[paymentService];
    this.paymentProduct = '/' + this.paymentProductSelect[0];
    this.updateBodyExample();
  }

  public handlePaymentProductChanged(paymentProduct: string) {
    this.paymentProduct = paymentProduct;
    this.updateBodyExample();
  }

  private updateBodyExample() {
    if (this.body) {
      if (this.paymentProduct.includes('pain')) {
        this.jsonService
          .getPreparedXmlData(this.paymentService + this.paymentProduct)
          .subscribe(data => {
            this.xml = true;
            this.body = vkbeautify.xml(data);
          });
      } else {
        this.jsonService
          .getPreparedJsonData(this.paymentService + this.paymentProduct)
          .subscribe(data => {
            this.xml = false;
            this.body = data;
          });
      }
    }
  }

  public onClear() {
    this.response = undefined;
    this.redirectUrl = undefined;
  }
}
