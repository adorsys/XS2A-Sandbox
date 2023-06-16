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
import { TppManagementService } from '../../services/tpp-management.service';
import { PageNavigationService } from '../../services/page-navigation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ADMIN_KEY } from '../../commons/constant/constant';
import { InfoService } from '../../commons/info/info.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TppUserService } from '../../services/tpp.user.service';
import { TooltipPosition } from '@angular/material/tooltip';

@Component({
  selector: 'app-admins',
  templateUrl: './admins.component.html',
  styleUrls: ['./admins.component.scss'],
})
export class AdminsComponent implements OnInit {
  admin: string;
  users: User[] = [];
  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };

  searchForm: UntypedFormGroup = this.formBuilder.group({
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });
  newPin: string;
  confirmNewPin: string;
  positionOptions: TooltipPosition[] = ['above', 'before', 'after', 'below', 'left', 'right'];
  position = new UntypedFormControl(this.positionOptions[0]);

  constructor(
    private userService: UserService,
    private infoService: InfoService,
    private pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private modalService: NgbModal,
    private router: Router,
    private userInfoService: TppUserService
  ) {}

  ngOnInit() {
    this.admin = sessionStorage.getItem(ADMIN_KEY);
    this.getAdmins();
    this.onQueryUsers();
  }

  pageChange(pageConfig: PageConfig) {
    this.listAdmins(pageConfig.pageNumber, pageConfig.pageSize);
  }

  changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }

  private getAdmins() {
    this.listAdmins(this.config.currentPageNumber, this.config.itemsPerPage);
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
        this.listAdmins(1, this.config.itemsPerPage);
      });
  }

  listAdmins(page: number, size: number) {
    this.tppManagementService.getAllAdmins(page - 1, size).subscribe((response: UserResponse) => {
      if (typeof response.users !== 'undefined') {
        this.users = response.users;
        this.users.reverse();
      }
      this.config.totalItems = response.totalElements;
    });
  }

  openConfirmation(content, userId: string, type: string) {
    this.modalService.open(content).result.then(() => {
      this.userInfoService.getUserInfo().subscribe((user: User) => {
        if (type === 'delete') {
          this.tppManagementService.deleteUser(userId).subscribe(() => {
            if (userId === user.id) {
              sessionStorage.removeItem('access_token');
              this.router.navigateByUrl('/login');
            } else {
              this.getAdmins();
            }
            this.infoService.openFeedback('Admin was successfully deleted!', {
              severity: 'info',
            });
          });
        } else if (type === 'pin' && this.newPin === this.confirmNewPin) {
          this.tppManagementService.changePin(userId, this.newPin).subscribe(() => {
            this.getAdmins();
            this.infoService.openFeedback('Pin was successfully changed!', {
              severity: 'info',
            });
          });
        }
      });
    });
  }
}
