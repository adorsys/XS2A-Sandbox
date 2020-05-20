import { Component, OnDestroy, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Account, AccountResponse } from '../../models/account.model';
import { Subscription } from 'rxjs';
import { map, tap, debounceTime } from 'rxjs/operators';
import {
  PageConfig,
  PaginationConfigModel,
} from '../../models/pagination-config.model';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { PageNavigationService } from '../../services/page-navigation.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { User } from '../../models/user.model';
import { TppUserService } from '../../services/tpp.user.service';
import { CountryService } from '../../services/country.service';
import { TppQueryParams } from '../../models/tpp-management.model';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss'],
})
// TODO Merge UsersComponent, TppsComponent and AccountListComponent into one single component https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/713
export class AccountListComponent implements OnInit, OnDestroy {
  admin = false;
  users: User[] = [];
  accounts: Account[] = [];
  subscription = new Subscription();
  countries: Array<object> = [];
  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };

  searchForm: FormGroup = this.formBuilder.group({
    ibanParam: '',
    tppId: '',
    tppLogin: '',
    country: '',
    blocked: '',
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });

  constructor(
    private accountService: AccountService,
    private formBuilder: FormBuilder,
    public router: Router,
    private countryService: CountryService,
    public pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private tppUserService: TppUserService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.getPageConfigs();
    this.getCountries();
    this.getCurrentData();
    this.onQueryUsers();
  }

  getAccounts(page: number, size: number, params: TppQueryParams) {
    if (this.admin === true) {
      this.tppManagementService
        .getAllAccounts(page - 1, size, params)
        .subscribe((response: AccountResponse) => {
          this.accounts = response.accounts;
          this.config.totalItems = response.totalElements;
        });
    } else if (this.admin === false) {
      this.accountService
        .getAccounts(page - 1, size, params.ibanParam)
        .subscribe((response: AccountResponse) => {
          this.accounts = response.accounts;
          this.config.totalItems = response.totalElements;
        });
    }
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
    this.subscription.unsubscribe();
  }

  onQueryUsers() {
    this.searchForm.valueChanges
      .pipe(
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

  private getTpps(page: number, size: number, queryParams: TppQueryParams) {
    this.tppManagementService
      .getTpps(page - 1, size, queryParams)
      .subscribe((response) => {
        this.users = response.tpps;
        this.config.totalItems = response.totalElements;
      });
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
    this.countryService.getCountryList().subscribe((data) => {
      this.countries = data;
    });
  }

  private getPageConfigs() {
    this.route.queryParams.subscribe((param) => {
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
    this.tppUserService.currentTppUser.subscribe((user: User) => {
      this.admin = user && user.userRoles.includes('SYSTEM');
      this.getAccounts(
        this.config.currentPageNumber,
        this.config.itemsPerPage,
        {
          userLogin: this.searchForm.get('ibanParam').value,
          tppId: this.searchForm.get('tppId').value,
          tppLogin: this.searchForm.get('tppLogin').value,
          country: this.searchForm.get('country').value,
          blocked: this.searchForm.get('blocked').value,
        }
      );
    });
  }
}
