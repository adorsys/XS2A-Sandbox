import {Component, OnInit} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {User} from "../../../models/user.model";
import {ActivatedRoute, Router} from "@angular/router";
import {AccountService} from '../../../services/account.service';
import {EmailVerificationService} from "../../../services/email-verification.service";
import {InfoService} from "../../../commons/info/info.service";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit {
  user: User;
  userId: string;

  constructor(private userService: UserService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private accService: AccountService,
              private emailVerificationService: EmailVerificationService,
              private infoService: InfoService) {
    this.user = new User();
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((param) => {
      this.userId = param['id'];
      this.getUserById();
    });
  }

  getUserById() {
    this.userService.getUser(this.userId).subscribe(
      (user: User) => this.user = user
    );
  }

  handleClickOnIBAN(event) {
    let iban = event.target.innerHTML.trim();
    this.accService.getAccountByIban(iban).subscribe(
      (account) => this.router.navigate(['/accounts/', account.id])
    );
  }

  confirmEmail(email: string) {
    this.emailVerificationService.sendEmailForVerification(email)
      .subscribe(
        () => this.infoService.openFeedback(`Confirmation letter has been sent to your email ${email}!`),
        error => {
          this.infoService.openFeedback("Sorry, something went wrong during the process of sending the confirmation!");
          console.log(JSON.stringify(error));
        })
  }

}
