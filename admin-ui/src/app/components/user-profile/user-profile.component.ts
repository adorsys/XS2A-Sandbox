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

import { User } from '../../models/user.model';
import { TppUserService } from '../../services/tpp.user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TppManagementService } from '../../services/tpp-management.service';
import { CountryService } from '../../services/country.service';
import { PageNavigationService } from '../../services/page-navigation.service';
import { AccountAccess } from '../../models/account-access.model';
import { InfoService } from '@commons/info/info.service';
import { FormControl, FormGroup } from '@angular/forms';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { TooltipPosition } from '@angular/material/tooltip';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public userForm: FormGroup;
  public bsModalRef: BsModalRef;
  tppUser: User;
  countries: any;
  userAmount = 0;
  private newPin = 'pin';
  positionOptions: TooltipPosition[] = [
    'above',
    'before',
    'after',
    'below',
    'left',
    'right',
  ];
  position = new FormControl(this.positionOptions[0]);

  constructor(
    public pageNavigationService: PageNavigationService,
    private countryService: CountryService,
    private userInfoService: TppUserService,
    private tppService: TppManagementService,
    private router: Router,
    private infoService: InfoService,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private modal: BsModalService
  ) {}

  ngOnInit(): void {
    this.setUpCountries();
    this.setUpCurrentUser();
    const tppId = this.route.snapshot.params['id'];
    if (tppId) {
      this.getUserInfo(tppId);
    }
  }

  private setUpCurrentUser() {
    this.userInfoService.getUserInfo().subscribe((user: User) => {
      this.tppUser = user;
    });
  }

  private setUpCountries() {
    this.countryService.getCountryCodes().subscribe((data) => {
      if (data !== null) {
        this.countries = data;
      }
    });
  }

  private getUserInfo(tppId: string) {
    this.tppService.getTppById(tppId).subscribe((user: User) => {
      if (user) {
        this.tppUser = user;
        this.countUsers(this.tppUser.accountAccesses, this.tppUser.id);
      } else {
        this.setUpCurrentUser();
      }
    });
  }

  openConfirmation(content, type: string) {
    this.modalService.open(content).result.then(() => {
      if (type === 'block') {
        this.blockTpp();
      } else if (type === 'delete') {
        this.delete();
      } else {
        this.changePin();
      }
    });
  }

  private blockTpp() {
    this.tppService.blockUser(this.tppUser.id).subscribe(() => {
      this.infoService.openFeedback('TPP was successfully blocked!', {
        severity: 'info',
      });
    });
  }

  private delete() {
    this.tppService.deleteTpp(this.tppUser.id).subscribe(() => {
      this.infoService.openFeedback('TPP was successfully deleted!', {
        severity: 'info',
      });
      this.router.navigateByUrl('/management');
    });
  }

  private changePin() {
    if (this.newPin && this.newPin !== '') {
      this.tppService.changePin(this.tppUser.id, this.newPin).subscribe(() => {
        this.infoService.openFeedback('TPP PIN was successfully changed!', {
          severity: 'info',
        });
      });
    }
  }

  private countUsers(accountAccesses: AccountAccess[], tppId: string) {
    if (accountAccesses && accountAccesses.length > 0) {
      this.tppService
        .getUsersForTpp(tppId)
        .toPromise()
        .then((users) => {
          const userSet = new Set<string>();
          users.forEach((value) => {
            userSet.add(value.id);
          });
          this.userAmount = userSet.size;
        });
    }
  }

  deleteRecoveryPointById() {
    this.infoService.openFeedback('Point successfully deleted');
  }

  resetPasswordViaEmail(login: string) {
    this.userInfoService.resetPasswordViaEmail(login).subscribe(() => {
      this.infoService.openFeedback(
        'Link for password reset was sent, check email.',
        {
          severity: 'info',
        }
      );
    });
  }

  handleBackNavigation() {
    window.history.back();
  }
}
