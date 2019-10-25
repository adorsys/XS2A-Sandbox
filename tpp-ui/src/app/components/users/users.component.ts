import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { debounceTime, tap } from 'rxjs/operators';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  searchForm: FormGroup;
  config: {itemsPerPage, currentPage, totalItems, maxSize} = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7,
  };

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.searchForm = this.formBuilder.group({
      query: ['', Validators.required],
      itemsPerPage: [this.config.itemsPerPage, Validators.required]
    });
    this.listUsers(this.config.currentPage, this.config.itemsPerPage);

    this.onQueryUsers();
  }

  listUsers(page: number, size: number, queryParam: string = '') {
    this.userService.listUsers(page - 1, size, queryParam).subscribe(response => {
      this.users = response.users;
      this.config.totalItems = response.totalElements;
    });
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.listUsers(pageNumber, this.config.itemsPerPage, this.searchForm.get('query').value);
  }

  onQueryUsers() {
    this.searchForm.valueChanges.pipe(
      tap(val => {
        this.searchForm.patchValue(val, { emitEvent: false });
      }),
      debounceTime(750)
    ).subscribe(form => {
      this.config.itemsPerPage = form.itemsPerPage;
      this.listUsers(1, this.config.itemsPerPage, form.query);
    });
  }

  public changePageSize(num: number): void {
    this.config.itemsPerPage = this.config.itemsPerPage + num;
  }
}
