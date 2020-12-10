import {Component, OnInit} from '@angular/core';
import {Subscription} from "rxjs/index";
import {PaymentTO} from "../../api/models/payment-to";
import {OnlineBankingService} from "../../common/services/online-banking.service";
import {InfoService} from "../../common/info/info.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {debounceTime, map, tap} from "rxjs/operators";
import {OnlineBankingConsentsService} from "../../api/services/online-banking-consents.service";

@Component({
  selector: 'app-periodic-payments',
  templateUrl: './periodic-payments.component.html',
  styleUrls: ['./periodic-payments.component.scss']
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
  private subscriptions: Subscription[] = [];

  constructor(private infoService: InfoService,
              private activatedRoute: ActivatedRoute,
              private fb: FormBuilder,
              private onlineBankingService: OnlineBankingService) {
  }

  ngOnInit() {
    this.formModel = this.fb.group({
      itemsPerPage: [this.config.itemsPerPage, Validators.required],
    });
    this.getPeriodicPaymentsPaged(this.config.currentPage, this.config.itemsPerPage);
    this.activatedRoute.params
      .pipe(map((resp) => resp.id))
      .subscribe(() => {
        this.refreshPeriodicPayments();
      });
    this.onQueryPeriodicPayments();
  }

  onQueryPeriodicPayments() {
    this.formModel.valueChanges
      .pipe(
        tap((val) => {
          this.formModel.patchValue(val, {emitEvent: false});
        }),
        debounceTime(750)
      )
      .subscribe((form) => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.getPeriodicPaymentsPaged(1, this.config.itemsPerPage);
      });
  }

  refreshPeriodicPayments() {
    this.getPeriodicPaymentsPaged(this.config.currentPage, this.config.itemsPerPage);
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.getPeriodicPaymentsPaged(pageNumber, this.config.itemsPerPage);
  }

  getPeriodicPaymentsPaged(page: number, size: number) {
    const params = {
      page: page - 1,
      size: size,
    } as OnlineBankingConsentsService.PagedUsingGetParams;
    this.onlineBankingService.getPeriodicPaymentsPaged(params).subscribe((response) => {
      this.payments = response.content;
      this.config.totalItems = response.totalElements;
    });
  }
}
