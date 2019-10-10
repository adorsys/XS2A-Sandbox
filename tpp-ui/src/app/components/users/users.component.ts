import { Component, OnInit } from '@angular/core';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users: User[];
  userFilter: any = {login: ''};
  config: {itemsPerPage, currentPage, totalItems, maxSize} = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7
  };

  constructor(private userService: UserService) {
    this.users = [];
  }

  ngOnInit() {
    this.listUsers(this.config.currentPage, this.config.itemsPerPage);
  }

  listUsers(page: number, size: number) {
    this.userService.listUsers(page - 1, size).subscribe(response => {
      this.users = response.users;
      this.config.totalItems = response.totalElements;
    });
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.listUsers(pageNumber, this.config.itemsPerPage);
  }
}
