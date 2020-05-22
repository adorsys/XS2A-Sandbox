import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, map, tap} from 'rxjs/operators';
import {User, UserResponse} from '../../models/user.model';
import {UserService} from '../../services/user.service';
import {
  PageConfig,
  PaginationConfigModel,
} from '../../models/pagination-config.model';
import {TppUserService} from '../../services/tpp.user.service';
import {TppManagementService} from '../../services/tpp-management.service';
import {PageNavigationService} from '../../services/page-navigation.service';
import {ActivatedRoute, Router} from '@angular/router';
import {TppQueryParams} from '../../models/tpp-management.model';
import {CountryService} from '../../services/country.service';
import {ADMIN_KEY} from '../../commons/constant/constant';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})

// TODO Merge UsersComponent, TppsComponent and AccountListComponent into one single component https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/713

export class UsersComponent implements OnInit {
  admin: string;
  users: User[] = [];
  countries: Array<object> = [];
  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };

  searchForm: FormGroup = this.formBuilder.group({
    userLogin: '',
    tppId: '',
    tppLogin: '',
    country: '',
    blocked: '',
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });

  constructor(
    private userService: UserService,
    private tppUserService: TppUserService,
    private countryService: CountryService,
    private pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {
  }

  ngOnInit() {
    this.admin = localStorage.getItem(ADMIN_KEY);
    this.getPageConfigs();
    this.getCountries();
    this.getUsers();
    this.onQueryUsers();
  }

  setBlocked(blocked) {
    this.searchForm.controls.blocked.patchValue(blocked);
  }

  createLastVisitedPageLink(id: string): string {
    this.pageNavigationService.setLastVisitedPage('/users/all');
    return `/profile/${id}/`;
  }

  pageChange(pageConfig: PageConfig) {
    const tppId = this.searchForm.get('tppId').value;
    if (tppId !== '') {
      this.listUsers(pageConfig.pageNumber, pageConfig.pageSize, {
        userLogin: this.searchForm.get('userLogin').value,
        tppId: tppId,
        tppLogin: this.searchForm.get('tppLogin').value,
        country: this.searchForm.get('country').value,
        blocked: this.searchForm.get('blocked').value,
      });
    } else {
      this.listUsers(
        pageConfig.pageNumber,
        pageConfig.pageSize,
        this.searchForm.get('userLogin').value
      );
    }
  }

  changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }

  showAllTpps() {
    this.searchForm.controls.userLogin.patchValue('');
    this.searchForm.controls.tppId.patchValue('');
    this.searchForm.controls.tppLogin.patchValue('');
    this.searchForm.controls.country.patchValue('');
    this.searchForm.controls.blocked.patchValue('');
    this.searchForm.controls.itemsPerPage.patchValue(this.config.itemsPerPage);
  }

  isSearchFormEmpty(): boolean {
    return (
      this.searchForm.controls.blocked.value === '' &&
      this.searchForm.controls.userLogin.value === '' &&
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

  private onQueryUsers() {
    this.searchForm.valueChanges
      .pipe(
        tap((val) => {
          this.searchForm.patchValue(val, {emitEvent: false});
        }),
        debounceTime(750)
      )
      .subscribe((form) => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.listUsers(1, this.config.itemsPerPage, {
          userLogin: form.userLogin,
          tppId: form.tppId,
          tppLogin: form.tppLogin,
          country: form.country,
          blocked: form.blocked,
        });
      });
  }

  listUsers(page: number, size: number, params: TppQueryParams) {
    if (this.admin === 'true') {
      this.tppManagementService
        .getAllUsers(page - 1, size, params)
        .subscribe((response: UserResponse) => {
          this.users = response.users;
          this.config.totalItems = response.totalElements;
        });
    } else if (this.admin === 'false') {
      this.userService
        .listUsers(page - 1, size, params.userLogin)
        .subscribe((response: UserResponse) => {
          this.users = response.users;
          this.config.totalItems = response.totalElements;
        });
    }
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

  private getUsers() {
    this.listUsers(
      this.config.currentPageNumber,
      this.config.itemsPerPage,
      {
        userLogin: this.searchForm.get('userLogin').value,
        tppId: this.searchForm.get('tppId').value,
        tppLogin: this.searchForm.get('tppLogin').value,
        country: this.searchForm.get('country').value,
        blocked: this.searchForm.get('blocked').value,
      });
  }
}
