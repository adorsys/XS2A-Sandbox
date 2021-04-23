import { Component, OnInit } from '@angular/core';
import { PaymentTO } from '../../api/models/payment-to';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { InfoService } from '../../common/info/info.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { debounceTime, map, tap } from 'rxjs/operators';
import { OnlineBankingConsentsService } from '../../api/services/online-banking-consents.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SCAPaymentResponseTO } from '../../api/models/scapayment-response-to';
import { AuthService } from '../../common/services/auth.service';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-periodic-payments',
  templateUrl: './periodic-payments.component.html',
  styleUrls: ['./periodic-payments.component.scss'],
})
export class PeriodicPaymentsComponent implements OnInit {
  formModel: FormGroup;
  config: {
    itemsPerPage: number;
    currentPage: number;
    totalItems: number;
    maxSize: number;
  } = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7,
  };
  payments: PaymentTO[];
  targetedPayment: PaymentTO;
  cancellationResponse: SCAPaymentResponseTO;
  selectedMethod: string;

  constructor(
    private infoService: InfoService,
    private activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    private modalService: NgbModal,
    private onlineBankingService: OnlineBankingService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.formModel = this.fb.group({
      itemsPerPage: [this.config.itemsPerPage, Validators.required],
    });
    this.getPeriodicPaymentsPaged(
      this.config.currentPage,
      this.config.itemsPerPage
    );
    this.activatedRoute.params.pipe(map((resp) => resp.id)).subscribe(() => {
      this.refreshPeriodicPayments();
    });
    this.onQueryPeriodicPayments();
  }

  onQueryPeriodicPayments() {
    this.formModel.valueChanges
      .pipe(
        tap((val) => {
          this.formModel.patchValue(val, { emitEvent: false });
        }),
        debounceTime(750)
      )
      .subscribe((form) => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.getPeriodicPaymentsPaged(1, this.config.itemsPerPage);
      });
  }

  refreshPeriodicPayments() {
    this.getPeriodicPaymentsPaged(
      this.config.currentPage,
      this.config.itemsPerPage
    );
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.getPeriodicPaymentsPaged(pageNumber, this.config.itemsPerPage);
  }

  getPeriodicPaymentsPaged(page: number, size: number) {
    const params = {
      page: page - 1,
      size,
    } as OnlineBankingConsentsService.PagedUsingGetParams;
    this.onlineBankingService
      .getPeriodicPaymentsPaged(params)
      .subscribe((response) => {
        this.payments = response.content;
        this.config.totalItems = response.totalElements;
      });
  }

  openCancelPaymentModal(
    content,
    contentSelect,
    contentTan,
    contentDeleted,
    payment: PaymentTO
  ) {
    this.targetedPayment = payment;
    this.modalService.open(content).result.then(
      () => {
        this.initCancel(
          contentSelect,
          contentTan,
          contentDeleted,
          this.targetedPayment.paymentId
        );
      },
      () => {}
    );
  }

  initCancel(contentSelect, contentTan, contentDeleted, paymentId: string) {
    this.onlineBankingService.startCancellation(paymentId).subscribe(
      (response) => {
        this.cancellationResponse = response;
        if (response.scaMethods.length === 0) {
          console.error('No Sca methods available! Cancellation not possible!');
          this.modalService.dismissAll('No Sca Available!');
          throw new HttpErrorResponse({
            status: 401,
            statusText: 'No Sca methods available! Cancellation not possible!',
          });
        }
        this.cancellationResponse.chosenScaMethod = this.cancellationResponse.scaMethods[0];
        this.authService.setAuthToken(response.bearerToken.access_token);
        this.openSelectMethodModal(contentSelect, contentTan, contentDeleted);
      },
      (error) => {
        console.error(error);
        throw new HttpErrorResponse({ error });
      }
    );
  }

  openSelectMethodModal(contentSelect, contentTan, contentDeleted) {
    this.modalService.open(contentSelect).result.then(
      () => {
        return this.startSca(contentTan, contentDeleted);
      },
      () => {}
    );
  }

  startSca(contentTan, contentDeleted) {
    return this.onlineBankingService
      .startScaNselectMethod(
        this.cancellationResponse.paymentId,
        this.cancellationResponse.chosenScaMethod.id,
        this.makeString()
      )
      .subscribe(
        (response) => {
          this.cancellationResponse = response;
          this.authService.setAuthToken(response.bearerToken.access_token);
          this.openTanModal(contentTan, contentDeleted);
        },
        (error) => {
          console.error(error);
          throw new HttpErrorResponse({ error });
        }
      );
  }

  openTanModal(contentTan, contentDeleted) {
    this.modalService.open(contentTan).result.then(
      () => {
        return this.validateNcancel(contentDeleted);
      },
      () => {}
    );
  }

  validateNcancel(contentDeleted) {
    return this.onlineBankingService
      .validateTanAndExecuteCancellation(
        this.cancellationResponse.paymentId,
        this.cancellationResponse.authConfirmationCode,
        this.cancellationResponse.authorisationId
      )
      .subscribe(
        (response) => {
          this.cancellationResponse = response;
          return this.openDeletedModal(contentDeleted);
        },
        (error) => {
          error.message = 'Wrong TAN! Cancellation aborted!';
          console.error(error);
          throw new HttpErrorResponse({ error });
        }
      );
  }

  openDeletedModal(contentDeleted) {
    this.modalService.open(contentDeleted).result.then(
      () => {
        this.refreshPeriodicPayments();
      },
      () => {}
    );
  }

  makeString(): string {
    let outString = '';
    const inOptions = 'abcdefghijklmnopqrstuvwxyz0123456789';
    for (let i = 0; i < 16; i++) {
      outString += inOptions.charAt(
        Math.floor(Math.random() * inOptions.length)
      );
    }
    this.cancellationResponse.authorisationId = outString;
    return outString;
  }

  handleMethodSelectedEvent(scaMethod: ScaUserDataTO) {
    this.cancellationResponse.chosenScaMethod = scaMethod;
  }

  handleTanInputEvent(target: string) {
    this.cancellationResponse.authConfirmationCode = target.toString();
  }
}
