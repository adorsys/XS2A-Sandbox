import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { map } from 'rxjs/operators';

import { InfoService } from '../../commons/info/info.service';
import { Account } from '../../models/account.model';
import { AccountReport } from '../../models/account-report';
import { UserAccess } from '../../models/user-access';
import { AccountService } from '../../services/account.service';
import { TppService } from '../../services/tpp.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss'],
})
export class AccountComponent implements OnInit {
  accountReport: AccountReport;
  accountID: string;

  constructor(
    private accountService: AccountService,
    private tppService: TppService,
    private activatedRoute: ActivatedRoute,
    private infoService: InfoService,
    private router: Router,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.activatedRoute.params
      .pipe(
        map(response => {
          return response.id;
        })
      )
      .subscribe((accountID: string) => {
        this.accountID = accountID;
        this.getAccountReport();
      });
  }

  public goToAccountDetail() {
    if (this.isAccountDeleted) {
      this.infoService.openFeedback(
        'You can not Grant Accesses to a Deleted/Blocked account',
        {
          severity: 'error',
        }
      );
    } else {
      this.router.navigate(['/accounts/' + this.account.id + '/access']);
    }
  }

  goToDepositCash() {
    if (!this.isAccountDeleted) {
      this.router.navigate(['/accounts/' + this.account.id + '/deposit-cash']);
    }
  }

  deleteAccountTransations() {
    this.tppService
      .deleteAccountTransations(this.account.id)
      .subscribe(() => {
        this.getAccountReport();
        this.infoService.openFeedback(
          `Transactions of ${this.account.iban} successfully deleted`,
          {
            severity: 'info',
          }
        );
      });
  }

  openDeleteConfirmation(content) {
    this.modalService.open(content).result.then(
      () => {
        this.deleteAccountTransations();
      },
      () => {}
    );
  }

  get isAccountDeleted(): boolean {
    if (this.account) {
      return (
        this.account.accountStatus === 'DELETED' ||
        this.account.accountStatus === 'BLOCKED'
      );
    }
    return false;
  }

  get account(): Account {
    return this.accountReport ? this.accountReport.details : null;
  }

  get accesses(): UserAccess[] {
    return this.accountReport ? this.accountReport.accesses : null;
  }

  getAccountReport() {
    this.accountService
      .getAccountReport(this.accountID)
      .subscribe((report: AccountReport) => {
        console.log(report);
        this.accountReport = report;
      });
  }
}
