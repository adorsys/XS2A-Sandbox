import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {TppUserService} from "../../services/tpp.user.service";
import { User } from 'src/app/models/user.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  public tppUser: User;

  constructor(private authService: AuthService,
      private userInfoService: TppUserService) { }

    ngOnInit(): void {
      if (this.authService.isLoggedIn()) {
          this.userInfoService.loadUserInfo().subscribe(
              (response: User) => {
                  this.tppUser = response;
                  console.log(this.tppUser);
              }
          );
      }
    }
}
