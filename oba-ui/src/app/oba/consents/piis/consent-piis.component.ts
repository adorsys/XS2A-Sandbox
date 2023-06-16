import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { debounceTime, takeUntil, tap } from 'rxjs/operators';
import { OnlineBankingService } from '../../../common/services/online-banking.service';
import { InfoService } from '../../../common/info/info.service';
import { Subject } from 'rxjs';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';
import { IPaginatorInterface } from '../../../common/interfaces/paginator.interface';
import { IPiisConsentContent } from '../../../common/interfaces/piisConsent.interface';
import { EConsentStatus } from '../../../common/enums/consent-status.enum';

@Component({
  selector: 'app-consent-piis',
  templateUrl: './consent-piis.component.html',
  styleUrls: ['./consent-piis.component.scss'],
})
export class ConsentPiisComponent implements OnInit, OnDestroy {
  formModel: UntypedFormGroup;
  consents: IPiisConsentContent[] = [];
  config: IPaginatorInterface = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7,
  };

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private onlineBankingService: OnlineBankingService,
    private fb: UntypedFormBuilder,
    private infoService: InfoService,
    private pisService: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService
  ) {}

  ngOnInit() {
    this.formInit();
    this.getPiisConsents(this.config.currentPage, this.config.itemsPerPage);
    this.onQueryConsents();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  formInit() {
    this.formModel = this.fb.group({
      itemsPerPage: [this.config.itemsPerPage, Validators.required],
    });
  }

  onQueryConsents() {
    this.formModel.valueChanges
      .pipe(
        takeUntil(this.ngUnsubscribe),
        tap((val) => {
          this.formModel.patchValue(val, { emitEvent: false });
        }),
        debounceTime(750)
      )
      .subscribe((form) => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.getPiisConsents(1, this.config.itemsPerPage);
      });
  }

  refreshConsents() {
    this.getPiisConsents(this.config.currentPage, this.config.itemsPerPage);
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.getPiisConsents(pageNumber, this.config.itemsPerPage);
  }

  getPiisConsents(page: number, size: number): void {
    const params = {
      currentPage: page - 1,
      itemsPerPage: size,
    };
    this.pisService
      .getPiisConsents(params)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        (res) => {
          this.consents = res.content;
          this.config.totalItems = res.totalElements;
        },
        (err) => {
          console.log('Error - ', err);
        }
      );
  }

  isConsentEnabled(consent: IPiisConsentContent) {
    return (
      consent.cmsPiisConsent.consentStatus === EConsentStatus.VALID ||
      consent.cmsPiisConsent.consentStatus === EConsentStatus.RECEIVED
    );
  }

  revokeConsent(consent: IPiisConsentContent) {
    if (!this.isConsentEnabled(consent)) {
      return false;
    }
    this.pisService
      .revokePiisConsents(consent.cmsPiisConsent.id)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((isSuccess) => {
        isSuccess
          ? this.getPiisConsents(
              this.config.currentPage,
              this.config.itemsPerPage
            )
          : this.infoService.openFeedback('could not revoke the consent', {
              severity: 'error',
            });
      });
  }
}
