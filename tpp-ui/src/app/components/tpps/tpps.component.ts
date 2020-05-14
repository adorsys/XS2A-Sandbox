import {Component, OnInit} from '@angular/core';
import {TppManagementService} from '../../services/tpp-management.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PageConfig, PaginationConfigModel} from '../../models/pagination-config.model';
import {AccountService} from '../../services/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {PageNavigationService} from '../../services/page-navigation.service';
import {debounceTime, map, tap} from 'rxjs/operators';
import {User} from '../../models/user.model';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CountryService} from '../../services/country.service';
import {TppQueryParams} from '../../models/tpp-management.model';
import {InfoService} from '../../commons/info/info.service';

@Component({
  selector: 'app-tpps',
  templateUrl: './tpps.component.html',
  styleUrls: ['./tpps.component.scss']
})
// TODO Merge UsersComponent, TppsComponent and AccountListComponent into one single component https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/-/issues/713
export class TppsComponent implements OnInit {

  tpps: User[] = [];
  countries: Array<string>;
  countriesList: Array<object> = [];
  newPin = 'pin';

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
    itemsPerPage: [this.config.itemsPerPage, Validators.required]
  });

  constructor(private accountService: AccountService,
              private formBuilder: FormBuilder,
              private tppManagementService: TppManagementService,
              private infoService: InfoService,
              public router: Router,
              public pageNavigationService: PageNavigationService,
              private countryService: CountryService,
              private modalService: NgbModal,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.getTpps(this.config.currentPageNumber, this.config.itemsPerPage, {});
    this.getCountries();
    this.getPageConfigs();
    this.onQueryUsers();
  }

  pageChange(pageConfig: PageConfig) {
    this.getTpps(pageConfig.pageNumber, pageConfig.pageSize, {
      userLogin: this.searchForm.get('userLogin').value,
      tppId: this.searchForm.get('tppId').value,
      tppLogin: this.searchForm.get('tppLogin').value,
      country: this.searchForm.get('country').value,
      blocked: this.searchForm.get('blocked').value
    });
  }

  setBlocked(blocked) {
    this.searchForm.controls.blocked.patchValue(blocked);
  }

  changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }

  createLastVisitedPageLink(id: string): string {
    this.pageNavigationService.setLastVisitedPage('/management');
    return `/profile/${id}`;
  }

  openConfirmation(content, tppId: string, type: string) {
    this.modalService.open(content).result.then(() => {
      if (type === 'block') {
        this.blockTpp(tppId);
      } else if (type === 'delete'){
        this.delete(tppId);
      } else {
        this.changePin(tppId);
      }
    }, () => {
    });
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
    return this.searchForm.controls.blocked.value === '' &&
      this.searchForm.controls.userLogin.value === '' &&
      this.searchForm.controls.tppId.value === '' &&
      this.searchForm.controls.tppLogin.value === '' &&
      this.searchForm.controls.country.value === '';
  }

  private getTpps(page: number, size: number, queryParams: TppQueryParams) {
    this.tppManagementService.getTpps(page - 1, size, queryParams).subscribe(response => {
      this.tpps = response.tpps;
      this.config.totalItems = response.totalElements;
    });
  }

  private onQueryUsers() {
    this.searchForm.valueChanges.pipe(
      tap(val => {
        this.searchForm.patchValue(val, {emitEvent: false});
      }),
      debounceTime(750)
    ).subscribe(form => {
      this.config.itemsPerPage = form.itemsPerPage;
      this.getTpps(1, this.config.itemsPerPage,
        {
          userLogin: form.userLogin,
          tppId: form.tppId,
          tppLogin: form.tppLogin,
          country: form.country,
          blocked: form.blocked,
        });
    });
  }

  private blockTpp(tppId: string) {
    this.tppManagementService.blockTpp(tppId).subscribe(() => {
      this.infoService.openFeedback('TPP was successfully blocked!', {severity: 'info'});
    });
  }

  private delete(tppId: string) {
    this.tppManagementService.deleteTpp(tppId).subscribe(() => {
      this.infoService.openFeedback('TPP was successfully deleted!', {severity: 'info'});
      this.getTpps(1, this.config.itemsPerPage,
        {
          userLogin: this.searchForm.get('userLogin').value,
          tppId: this.searchForm.get('tppId').value,
          tppLogin: this.searchForm.get('tppLogin').value,
          country: this.searchForm.get('country').value,
          blocked: this.searchForm.get('blocked').value
        });
    });
  }

  private changePin(tppId: string) {
    if (this.newPin && this.newPin !== '') {
      this.tppManagementService.changePin(tppId, this.newPin).subscribe(() => {
        this.infoService.openFeedback('TPP PIN was successfully changed!', {severity: 'info'});
        this.getTpps(1, this.config.itemsPerPage,
          {
            userLogin: this.searchForm.get('userLogin').value,
            tppId: this.searchForm.get('tppId').value,
            tppLogin: this.searchForm.get('tppLogin').value,
            country: this.searchForm.get('country').value,
            blocked: this.searchForm.get('blocked').value
          });
      });
    }
  }

  private getCountries() {
    this.countryService.getCountryList().subscribe(
      data => {
        this.countriesList = data;
      });

    this.countryService.currentCountries.subscribe(
      data => {
        if (data !== null) {
          this.countries = data;
        }
      });
  }

  private getPageConfigs() {
    this.route.queryParams.pipe(
      map(params => params.page))
      .subscribe(param => {
        if (param) {
          this.config.currentPageNumber = param;
        } else {
          this.config.currentPageNumber = 1;
        }
      });

  }
}
