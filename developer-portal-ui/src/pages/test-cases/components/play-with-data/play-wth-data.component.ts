import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../../../services/rest.service';
import { DataService } from '../../../../services/data.service';
import { getStatusText } from 'http-status-codes';

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
  @Input() paymentServiceFlag: boolean;
  @Input() paymentProductFlag: boolean;
  @Input() paymentIdFlag: boolean;
  response: object = {};
  paymentService = 'payments';
  paymentProduct = '/sepa-credit-transfers';
  paymentId = 'paymentId';
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

  constructor(
    public restService: RestService,
    public dataService: DataService
  ) {}

  /**
   * Get status text by status code
   * using http-status-codes library
   */
  getStatusText(status) {
    return getStatusText(status);
  }

  sendRequest() {
    this.dataService.isLoading = true;

    // TODO check if request has body or make switch case for get, post, delete and put

    const respBodyEl = document.getElementById('textArea');
    if (this.isValidJSONString(respBodyEl['value'])) {
      const bodyValue = JSON.parse(respBodyEl['value']);
      this.restService
        .sendRequest(
          this.method,
          this.url + this.paymentService + this.paymentProduct,
          bodyValue,
          this.headers
        )
        .subscribe(
          resp => {
            this.response = Object.assign(resp);
            this.dataService.isLoading = false;
            this.dataService.showToast('Request sent', 'Success!', 'success');
            console.log('response:', JSON.stringify(this.response));
          },
          err => {
            this.dataService.isLoading = false;
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
      this.dataService.isLoading = false;
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

  ngOnInit() {
    console.table(
      this.method,
      this.url,
      this.paymentServiceFlag,
      this.paymentProductFlag
    );
    this.paymentService = this.paymentServiceFlag ? 'payments' : '';
    this.paymentProduct = this.paymentProductFlag
      ? '/sepa-credit-transfers'
      : '';
    console.table(this.url, this.paymentService, this.paymentProduct);
  }
}
