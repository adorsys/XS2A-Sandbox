/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators, UntypedFormControl } from '@angular/forms';
import { debounceTime, tap } from 'rxjs/operators';
import { User, UserResponse } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { PageConfig, PaginationConfigModel } from '../../models/pagination-config.model';
import { TppUserService } from '../../services/tpp.user.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { PageNavigationService } from '../../services/page-navigation.service';
import { ActivatedRoute } from '@angular/router';
import { TppQueryParams } from '../../models/tpp-management.model';
import { CountryService } from '../../services/country.service';
import { ADMIN_KEY } from '../../commons/constant/constant';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { InfoService } from '../../commons/info/info.service';
import { TooltipPosition } from '@angular/material/tooltip';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})

// TODO Merge UsersComponent, TppsComponent and AccountListComponent into one single component
//  https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/713
export class UsersComponent implements OnInit {
  admin: string;
  statusBlock: string;
  users: User[] = [];
  countries: Array<object> = [];
  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };
  positionOptions: TooltipPosition[] = ['above', 'before', 'after', 'below', 'left', 'right'];
  position = new UntypedFormControl(this.positionOptions[0]);
  searchForm: UntypedFormGroup = this.formBuilder.group({
    userLogin: '',
    tppId: '',
    tppLogin: '',
    country: '',
    blocked: '',
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });

  constructor(
    private userService: UserService,
    private infoService: InfoService,
    private tppUserService: TppUserService,
    private countryService: CountryService,
    private pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.admin = sessionStorage.getItem(ADMIN_KEY);
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
      this.listUsers(pageConfig.pageNumber, pageConfig.pageSize, this.searchForm.get('userLogin').value);
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
          this.searchForm.patchValue(val, { emitEvent: false });
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
      this.tppManagementService.getAllUsers(page - 1, size, params).subscribe((response: UserResponse) => {
        if (typeof response.users !== 'undefined') {
          this.users = response.users;
          this.users.reverse();
        } else if (typeof response.users === 'undefined') {
          this.users = response.users;
        }
        this.config.totalItems = response.totalElements;
      });
    } else if (this.admin === 'false') {
      this.userService.listUsers(page - 1, size, params.userLogin).subscribe((response: UserResponse) => {
        if (typeof response.users !== 'undefined') {
          this.users = response.users;
          this.users.reverse();
          this.config.totalItems = response.totalElements;
        }
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
    this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {
      userLogin: this.searchForm.get('userLogin').value,
      tppId: this.searchForm.get('tppId').value,
      tppLogin: this.searchForm.get('tppLogin').value,
      country: this.searchForm.get('country').value,
      blocked: this.searchForm.get('blocked').value,
    });
  }

  openConfirmation(content, userId: string, type: string) {
    this.statusBlock = type;
    this.modalService.open(content).result.then(() => {
      if (type === 'block') {
        this.blockUser(userId);
      } else if (type === 'unblock') {
        this.blockUser(userId);
      } else if (type === 'delete') {
        this.delete(userId);
      }
    });
  }

  private blockUser(userId: string) {
    if (this.admin === 'true') {
      this.tppManagementService.blockUser(userId).subscribe(() => {
        if (this.statusBlock === 'block') {
          this.infoService.openFeedback('User was successfully unblocked!', {
            severity: 'info',
          });
        }
        this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {});
      });
      if (this.statusBlock === 'unblock') {
        this.infoService.openFeedback('User was successfully blocked!', {
          severity: 'info',
        });
      }
    } else if (this.admin === 'false') {

      this.userService.blockTpp(userId).subscribe(() => {
        if (this.statusBlock === 'block') {
          this.infoService.openFeedback('User was successfully unblocked!', {
            severity: 'info',
          });
        }
        this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {});

      });
      if (this.statusBlock === 'unblock') {
        this.infoService.openFeedback('User was successfully blocked!', {
          severity: 'info',
        });
        this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {});
      }
    }
  }

  private delete(userId: string) {
    if (this.admin === 'true') {
      this.tppManagementService.deleteUser(userId).subscribe(() => {
        this.infoService.openFeedback('User was successfully deleted!', {
          severity: 'info',
        });
        this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {});
      });
    } else if (this.admin === 'false') {
      this.userService.deleteUser(userId).subscribe(() => {
        this.infoService.openFeedback('User was successfully deleted!', {
          severity: 'info',
        });
        this.listUsers(this.config.currentPageNumber, this.config.itemsPerPage, {});
      });
    }
  }
}
