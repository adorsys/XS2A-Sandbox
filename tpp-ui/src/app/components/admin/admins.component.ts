import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, tap} from 'rxjs/operators';
import {User, UserResponse} from '../../models/user.model';
import {UserService} from '../../services/user.service';
import {PageConfig, PaginationConfigModel,} from '../../models/pagination-config.model';
import {TppManagementService} from '../../services/tpp-management.service';
import {PageNavigationService} from '../../services/page-navigation.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ADMIN_KEY} from '../../commons/constant/constant';
import {InfoService} from '../../commons/info/info.service';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {TppUserService} from "../../services/tpp.user.service";

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

  searchForm: FormGroup = this.formBuilder.group({
    itemsPerPage: [this.config.itemsPerPage, Validators.required],
  });

  constructor(
    private userService: UserService,
    private infoService: InfoService,
    private pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private modalService: NgbModal,
    private router: Router,
    private userInfoService: TppUserService
  ) {
  }

  ngOnInit() {
    this.admin = localStorage.getItem(ADMIN_KEY);
    this.getAdmins();
    this.onQueryUsers();
  }

  pageChange(pageConfig: PageConfig) {
    this.listAdmins(
      pageConfig.pageNumber,
      pageConfig.pageSize);
  }

  changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }

  private getAdmins() {
    this.listAdmins(
      this.config.currentPageNumber,
      this.config.itemsPerPage);
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
        this.listAdmins(1, this.config.itemsPerPage);
      });
  }

  listAdmins(page: number, size: number,) {
    this.tppManagementService
      .getAllAdmins(page - 1, size)
      .subscribe((response: UserResponse) => {
        this.users = response.users;
        this.config.totalItems = response.totalElements;
      });

  }

  openConfirmation(content, userId: string, type: string) {
    this.modalService.open(content).result.then(
      () => {
        if (type === 'delete') {
          this.userInfoService.getUserInfo().subscribe((user: User) => {
            this.tppManagementService.deleteUser(userId).subscribe(() => {
              this.infoService.openFeedback('Admin was successfully deleted!', {
                severity: 'info',
              });
              if (userId === user.id) {
                localStorage.removeItem('access_token');
                this.router.navigateByUrl('/login');
              } else {
                this.getAdmins();
              }
            });
          });
        }
      },
      () => {
      }
    );
  }
}
