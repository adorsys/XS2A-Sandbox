import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {User} from "../../models/user.model";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

    users: User[];
    userFilter: any = {login: ''};

    constructor(private userService: UserService) {
    }

    ngOnInit() {
        this.listUsers();
    }

    listUsers() {
        this.userService.listUsers().subscribe((users: User[]) => {
            this.users = users;
        })
    }
}
