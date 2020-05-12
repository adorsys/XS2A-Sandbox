import {Component, OnInit} from '@angular/core';

import {User} from '../../models/user.model';
import {TppUserService} from '../../services/tpp.user.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TppManagementService} from '../../services/tpp-management.service';
import {CountryService} from '../../services/country.service';
import {PageNavigationService} from '../../services/page-navigation.service';
import {AccountAccess} from '../../models/account-access.model';
import {InfoService} from '../../commons/info/info.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  admin;
  tppUser: User;
  countries;
  userAmount = 0;
  private newPin = 'pin';

  constructor(private countryService: CountryService,
              private userInfoService: TppUserService,
              private tppService: TppManagementService,
              private router: Router,
              private infoService: InfoService,
              private pageNavigationService: PageNavigationService,
              private route: ActivatedRoute,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.setUpCountries();
    this.setUpCurrentUser();

    const tppId = this.route.snapshot.params['id'];
    if (tppId) {
      this.getUserInfo(tppId);
    }
  }

  private setUpCurrentUser() {
    this.userInfoService.currentTppUser.subscribe(
      (user: User) => {
        this.admin = user && user.userRoles.includes('SYSTEM');
        this.tppUser = user;
      });
  }

  private setUpCountries() {
    this.countryService.currentCountries.subscribe(
      data => {
        if (data !== null) {
          this.countries = data;
        }
      });
  }

  private getUserInfo(tppId: string) {
    this.tppService.getTppById(tppId).subscribe(
      (user: User) => {
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
      } else if (type === 'delete'){
        this.delete();
      } else {
        this.changePin();
      }
    }, () => {
    });
  }

  private blockTpp() {
    this.tppService.blockTpp(this.tppUser.id).subscribe(() => {
      this.infoService.openFeedback('TPP was successfully blocked!', {severity: 'info'});
    });
  }

  private delete() {
    if (this.admin) {
      this.tppService.deleteTpp(this.tppUser.id).subscribe(() => {
        this.infoService.openFeedback('TPP was successfully deleted!', {severity: 'info'});
        this.router.navigateByUrl('/management');
      });
    } else {
      this.tppService.deleteSelf().subscribe(() => {
        localStorage.removeItem('access_token');
        this.router.navigateByUrl('/login');
      });
    }

  }

  private changePin() {
    if (this.newPin && this.newPin !== '') {
      this.tppService.changePin(this.tppUser.id, this.newPin).subscribe(() => {
        this.infoService.openFeedback('TPP PIN was successfully changed!', {severity: 'info'});
      });
    }
  }

  private countUsers(accountAccesses: AccountAccess[], tppId: string) {
    if (accountAccesses && accountAccesses.length > 0) {
      this.tppService.getUsersForTpp(tppId).toPromise().then(users => {
          let userSet = new Set<string>();
          users.forEach(value => {
            userSet.add(value.id);
          });
          this.userAmount = userSet.size;
        }
      );
    }
  }
}
