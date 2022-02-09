/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
import { AccountService } from '../../services/account.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Account, AccountResponse } from '../../models/account.model';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil, tap } from 'rxjs/operators';
import {
  PageConfig,
  PaginationConfigModel,
} from '../../models/pagination-config.model';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { PageNavigationService } from '../../services/page-navigation.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { User } from '../../models/user.model';
import { TppUserService } from '../../services/tpp.user.service';
import { CountryService } from '../../services/country.service';
import { TppQueryParams } from '../../models/tpp-management.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { InfoService } from '@commons/info/info.service';
import { TooltipPosition } from '@angular/material/tooltip';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss'],
})
export class AccountListComponent implements OnInit, OnDestroy {
  users: User[] = [];
  statusBlock: string;
  accounts: Account[] = [];
  countries: Array<object> = [];
  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };
  positionOptions: TooltipPosition[] = [
    'above',
    'before',
    'after',
    'below',
    'left',
    'right',
  ];
  position = new FormControl(this.positionOptions[0]);
  searchForm: FormGroup = this.formBuilder.group({
    ibanParam: '',
    tppId: '',
    tppLogin: '',
    country: '',
    blocked: '',
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });

  private onDestroy = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private formBuilder: FormBuilder,
    public router: Router,
    private countryService: CountryService,
    public pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private infoService: InfoService,
    private tppUserService: TppUserService,
    private route: ActivatedRoute,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.getPageConfigs();
    this.getCountries();
    this.getCurrentData();
    this.onQueryUsers();
  }

  getAccounts(page: number, size: number, params: TppQueryParams) {
    this.tppManagementService
      .getAllAccounts(page - 1, size, params)
      .subscribe((response: AccountResponse) => {
        this.accounts = response.accounts;
        this.config.totalItems = response.totalElements;
      });
  }

  goToDepositCash(account: Account) {
    if (!this.isAccountEnabled(account)) {
      return false;
    }
    this.router.navigate(['/accounts/' + account.id + '/deposit-cash']);
  }

  isAccountEnabled(account: Account): boolean {
    return account.accountStatus !== 'DELETED';
  }

  pageChange(pageConfig: PageConfig) {
    const tppId = this.searchForm.get('tppId').value;
    this.getAccounts(pageConfig.pageNumber, pageConfig.pageSize, {
      ibanParam: this.searchForm.get('ibanParam').value,
      tppId: tppId,
      tppLogin: this.searchForm.get('tppLogin').value,
      country: this.searchForm.get('country').value,
      blocked: this.searchForm.get('blocked').value,
    });
  }

  ngOnDestroy(): void {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  onQueryUsers() {
    this.searchForm.valueChanges
      .pipe(
        takeUntil(this.onDestroy),
        tap((val) => {
          this.searchForm.patchValue(val, { emitEvent: false });
        }),
        debounceTime(750)
      )
      .subscribe((form) => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.getAccounts(1, this.config.itemsPerPage, {
          ibanParam: form.ibanParam,
          tppId: form.tppId,
          tppLogin: form.tppLogin,
          country: form.country,
          blocked: form.blocked,
        });
      });
  }

  changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }

  createAccountDetailsLink(id: string): string {
    const baseLink = '/accounts/';
    this.pageNavigationService.setLastVisitedPage(baseLink);
    return `${baseLink}${id}`;
  }

  createLastVisitedPageLink(id: string): string {
    this.pageNavigationService.setLastVisitedPage('/accounts');
    return `/profile/${id}`;
  }

  setBlocked(blocked) {
    this.searchForm.controls.blocked.patchValue(blocked);
  }

  showAllTpps() {
    this.searchForm.controls.ibanParam.patchValue('');
    this.searchForm.controls.tppId.patchValue('');
    this.searchForm.controls.tppLogin.patchValue('');
    this.searchForm.controls.country.patchValue('');
    this.searchForm.controls.blocked.patchValue('');
    this.searchForm.controls.itemsPerPage.patchValue(this.config.itemsPerPage);
  }

  isSearchFormEmpty(): boolean {
    return (
      this.searchForm.controls.blocked.value === '' &&
      this.searchForm.controls.ibanParam.value === '' &&
      this.searchForm.controls.tppId.value === '' &&
      this.searchForm.controls.tppLogin.value === '' &&
      this.searchForm.controls.country.value === ''
    );
  }

  private getCountries() {
    this.countryService
      .getCountryList()
      .pipe(takeUntil(this.onDestroy))
      .subscribe((data) => {
        this.countries = data;
      });
  }

  private getPageConfigs() {
    this.route.queryParams
      .pipe(takeUntil(this.onDestroy))
      .subscribe((param) => {
        if (param.page) {
          this.config.currentPageNumber = param.page;
        } else {
          this.config.currentPageNumber = 1;
        }

        if (param.tppId) {
          this.searchForm.controls.tppId.patchValue(param.tppId);
        }
      });
  }

  private getCurrentData() {
    this.getAccounts(this.config.currentPageNumber, this.config.itemsPerPage, {
      userLogin: this.searchForm.get('ibanParam').value,
      tppId: this.searchForm.get('tppId').value,
      tppLogin: this.searchForm.get('tppLogin').value,
      country: this.searchForm.get('country').value,
      blocked: this.searchForm.get('blocked').value,
    });
  }

  openConfirmation(content, tppId: string, type: string) {
    this.statusBlock = type;
    this.modalService.open(content).result.then(
      () => {
        if (type === 'block') {
          this.blockAccount(tppId);
        } else if (type === 'unblock') {
          this.blockAccount(tppId);
        } else if (type === 'delete') {
          this.delete(tppId);
        }
      },
      () => {}
    );
  }

  private blockAccount(accountId: string) {
    this.tppManagementService
      .blockAccount(accountId)
      .pipe(takeUntil(this.onDestroy))
      .subscribe(() => {
        if (this.statusBlock === 'block') {
          this.infoService.openFeedback('Account was successfully unblocked!', {
            severity: 'info',
          });
        }
        this.getAccounts(
          this.config.currentPageNumber,
          this.config.itemsPerPage,
          {}
        );
      });
    if (this.statusBlock === 'unblock') {
      this.infoService.openFeedback('Account was successfully blocked!', {
        severity: 'info',
      });
      this.getAccounts(
        this.config.currentPageNumber,
        this.config.itemsPerPage,
        {}
      );
    }
  }

  private delete(accountId: string) {
    this.tppManagementService
      .deleteAccount(accountId)
      .pipe(takeUntil(this.onDestroy))
      .subscribe(() => {
        this.infoService.openFeedback('Account was successfully deleted!', {
          severity: 'info',
        });
        this.getAccounts(
          this.config.currentPageNumber,
          this.config.itemsPerPage,
          {}
        );
      });
  }
}
