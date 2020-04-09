import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
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
import { PaymentType, PaymentTypesMatrix } from '../../../../models/paymentTypesMatrix.model';
import { AcceptType } from '../../../../models/acceptType.model';
import * as uuid from 'uuid';
import { GoogleAnalyticsService } from '../../../../services/google-analytics.service';
import { CertificateService } from '../../../../services/certificate.service';
import { EVENT_VALUE, SLICE_DATE_FROM_ISO_STRING } from '../../../common/constant/constants';

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

  @Input() resourceIds = [];
  @Input() acceptFlag: boolean;

  bookingStatus = '';
  redirectUrl = '';
  dateFrom = '';
  xml = false;

  paymentServiceSelect = [];
  paymentProductSelect = [];
  bookingStatusSelect = [];
  acceptTypes = [];
  selectedConsentType = 'dedicatedAccountsConsent';

  paymentTypesMatrix: PaymentTypesMatrix;
  paymentTypes = [PaymentType.single, PaymentType.bulk, PaymentType.periodic];
  acceptHeader;
  certificate: string;
  private disabledHeaders = [];
  booleanValues = ['true', 'false'];

  @Input() eventName: string;
  @Input() eventCategory: string;
  @Input() eventAction: string;
  @Input() eventLabel: string;

  default = true;

  constructor(
    public restService: RestService,
    public dataService: DataService,
    public copyService: CopyService,
    public jsonService: JsonService,
    public aspspService: AspspService,
    private http: HttpClient,
    private certificateService: CertificateService,
    private googleAnalyticsService: GoogleAnalyticsService
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
    this.sendGoogleAnalytics();

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
      this.finalUrl += this.bookingStatus ? '&bookingStatus=' + this.bookingStatus : '';
      this.finalUrl += this.transactionId ? '/' + this.transactionId : '';
    }

    const respBodyEl: any = this.xml ? document.getElementById('textAreaXml') : document.getElementById('textArea');
    const requestBody = respBodyEl ? respBodyEl.value.toString() : {};

    this.restService.sendRequest(this.method, this.finalUrl, this.buildHeadersForRequest(), this.acceptHeader, requestBody).subscribe(
      (resp) => {
        if (this.acceptHeader === AcceptType.xml) {
          resp.body = vkbeautify.xml(resp.body);
        }

        this.response = Object.assign(resp);

        if (this.response.body.hasOwnProperty('_links') && this.response.body._links.hasOwnProperty('scaRedirect')) {
          this.redirectUrl = this.response.body._links.scaRedirect.href;
        } else if (this.response.body.hasOwnProperty('paymentId')) {
          this.paymentId = this.response.body.paymentId;
          LocalStorageService.set('paymentId', this.response.body.paymentId);
        } else if (this.response.body.hasOwnProperty('authorisationId')) {
          this.authorisationId = this.response.body.authorisationId;
          LocalStorageService.set('authorisationId', this.authorisationId);
        } else if (this.response.body.hasOwnProperty('consentId')) {
          this.consentId = this.response.body.consentId;
          LocalStorageService.set('consentId', this.consentId);
        } else if (this.response.body.hasOwnProperty('cancellationId')) {
          this.cancellationId = this.response.body.cancellationId;
          LocalStorageService.set('cancellationId', this.cancellationId);
        } else if (this.response.body.hasOwnProperty('accountId')) {
          this.accountId = this.response.body.accountId;
          LocalStorageService.set('accountId', this.accountId);
        } else if (this.response.body.hasOwnProperty('transactionId')) {
          this.transactionId = this.response.body.transactionId;
          LocalStorageService.set('transactionId', this.transactionId);
        } else if (this.response.body.hasOwnProperty('accounts')) {
          for (const a of this.response.body.accounts) {
            const id = a.resourceId;
            if (id) {
              this.resourceIds.push(id);
            }
          }
        }
        this.dataService.setIsLoading(false);
        this.dataService.showToast('Request sent', 'Success!', 'success');
      },
      (err) => {
        this.dataService.setIsLoading(false);
        this.dataService.showToast('Something went wrong!', 'Error!', 'error');
        if (this.acceptHeader === AcceptType.xml) {
          err.error = vkbeautify.xml(err.error);
        }

        this.response = Object.assign(err);
        console.log('err', JSON.stringify(err));
      }
    );
  }

  // Fixing the loss of input focus
  trackByFn(index: any) {
    return index;
  }

  handleConsentSelected(consentType: string) {
    this.body = this.consentTypes[consentType];
  }

  ngOnInit() {
    this.aspspService.getAspspProfile().subscribe((data) => {
      if (data.pis.supportedPaymentTypeAndProductMatrix) {
        this.paymentTypesMatrix = data.pis.supportedPaymentTypeAndProductMatrix;
        this.setPaymentServicesAndProducts();
        this.setBookingStatuses(data.ais.transactionParameters.availableBookingStatuses);
        this.setDefaultFields();
        this.setAcceptTypes(data.ais.transactionParameters.supportedTransactionApplicationTypes);
      }

      if (this.headers) {
        this.setDefaultHeaders();
      }
    });

    this.certificate = this.certificateService.getStoredCertificate();

    this.certificateService.currentDefault.subscribe((data) => (this.default = data));
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

  public onClear() {
    this.response = undefined;
    this.redirectUrl = undefined;
    this.paymentId = '';
    this.accountId = '';
    this.authorisationId = '';
    this.cancellationId = '';
  }

  public disableHeader(event: any) {
    const checkbox = event.target;
    if (checkbox) {
      const value = checkbox.value;
      const input = document.getElementById(value);

      if (input) {
        const attributeName = 'disabled';

        if (checkbox.checked) {
          input.removeAttribute(attributeName);
          this.disabledHeaders = this.disabledHeaders.filter((v) => v !== value);
          if (value === 'TPP-QWAC-Certificate') {
            this.certificateService.setDefault(false);
          }
        } else {
          input.setAttribute(attributeName, 'true');
          this.disabledHeaders.push(value);
          if (value === 'TPP-QWAC-Certificate') {
            this.certificateService.setDefault(true);
          }
        }
      }
    }
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
    if (bookingStatuses && this.bookingStatusFlag && bookingStatuses.length > 0) {
      this.bookingStatus = bookingStatuses[0];
      this.bookingStatusSelect = bookingStatuses;
    } else {
      this.bookingStatus = '';
    }
  }

  private setAcceptTypes(supportedTransactionApplicationTypes: Array<string>) {
    if (this.acceptFlag && supportedTransactionApplicationTypes && supportedTransactionApplicationTypes.length > 0) {
      this.acceptTypes = supportedTransactionApplicationTypes;
      this.acceptHeader = supportedTransactionApplicationTypes[0];
    } else {
      this.acceptHeader = '';
    }
  }

  private setDefaultFields() {
    this.paymentId = this.paymentIdFlag ? this.paymentId : '';
    this.cancellationId = this.cancellationIdFlag ? this.cancellationId : '';
    this.consentId = this.consentIdFlag ? this.consentId : '';
    this.authorisationId = this.authorisationIdFlag ? this.authorisationId : '';
    this.accountId = this.accountIdFlag ? this.accountId : '';
    this.transactionId = this.transactionIdFlag ? this.transactionId : '';

    this.dateFrom = this.dateFromFlag ? new Date().toISOString().slice(0, SLICE_DATE_FROM_ISO_STRING) : '';
    this.fieldsToCopy = this.fieldsToCopy ? this.fieldsToCopy : [];
  }

  private updateBodyExample() {
    if (this.body) {
      if (this.paymentProduct.includes('pain')) {
        this.jsonService.getPreparedXmlData(this.paymentService + this.paymentProduct).subscribe((data) => {
          if (data && data !== '') {
            this.xml = true;
            this.body = vkbeautify.xml(data);
          }
        });
      } else {
        this.jsonService
          .getPreparedJsonData(this.paymentService + this.paymentProduct, this.paymentProduct === '/sepa-credit-transfers')
          .subscribe((data) => {
            this.xml = false;
            this.body = data;
          });
      }
    }
  }

  private setDefaultHeaders() {
    this.headers['TPP-QWAC-Certificate'] = this.certificate;

    if (this.default) {
      this.disabledHeaders['TPP-QWAC-Certificate'] = this.certificate;
    }

    this.headers['X-Request-ID'] = uuid.v4();
    this.setIpAddress();
  }

  private setIpAddress() {
    return this.http.get('https://api.ipify.org/?format=json').subscribe(
      (ip) => (this.headers['PSU-IP-Address'] = ip['ip']),
      () => (this.headers['PSU-IP-Address'] = '1.1.1.1')
    );
  }

  private buildHeadersForRequest() {
    if (this.headers) {
      const requestHeaders = {};

      for (const key of Object.keys(this.headers)) {
        if (this.headers[key]) {
          requestHeaders[key] = this.headers[key];
        }
      }

      requestHeaders['Content-Type'] = this.xml ? 'application/xml' : 'application/json';
      requestHeaders['Accept'] = this.acceptHeader ? this.acceptHeader : 'application/json';

      for (const disabled of this.disabledHeaders) {
        delete requestHeaders[disabled];
      }

      return new HttpHeaders(requestHeaders);
    }
  }

  isBooleanValue(item: any) {
    return item.value === 'true' || item.value === 'false';
  }

  changeBooleanHeader(key: any, value: any) {
    this.headers[key] = value;
  }

  private sendGoogleAnalytics() {
    if (this.googleAnalyticsService.enabled) {
      this.googleAnalyticsService.eventEmitter(this.eventName, this.eventCategory, this.eventAction, this.eventLabel, EVENT_VALUE);
    }
  }

  updateCertificate($event) {
    this.headers['TPP-QWAC-Certificate'] = $event;
  }
}
