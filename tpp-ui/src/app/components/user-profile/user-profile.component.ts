import {Component, OnInit} from '@angular/core';

import {User} from '../../models/user.model';
import {AuthService} from '../../services/auth.service';
import {TppUserService} from '../../services/tpp.user.service';
import {TppService} from "../../services/tpp.service";
import {Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  public tppUser: User;

  constructor(private authService: AuthService,
              private userInfoService: TppUserService,
              private tppService: TppService,
              private router: Router,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.userInfoService.getUserInfo().subscribe(
        (response: User) => {
          this.tppUser = response;
          console.log(this.tppUser);
        }
      );
    }
  }

  deleteTpp() {
    this.tppService.deleteTpp().subscribe(() => {
      localStorage.removeItem('access_token');
      this.router.navigate(['/login']);
    });
  }

  openDeleteConfirmation(content) {
    this.modalService.open(content).result.then(() => {
      this.deleteTpp();
    }, () => {
    });
  }
}
