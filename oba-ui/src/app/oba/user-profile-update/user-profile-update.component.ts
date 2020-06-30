import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { Router } from '@angular/router';
import { UserTO } from '../../api/models/user-to';
import { InfoService } from '../../common/info/info.service';
import { CurrentUserService } from '../../common/services/current-user.service';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-user-profile-edit',
  templateUrl: './user-profile-update.component.html',
  styleUrls: ['./user-profile-update.component.scss'],
})
export class UserProfileUpdateComponent implements OnInit {
  public obaUser: UserTO;
  public submitted: boolean;
  public userForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private currentUserService: CurrentUserService,
    private onlineBankingService: OnlineBankingService,
    private shareDataService: ShareDataService,
    private currentUser: CurrentUserService,
    private infoService: InfoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.setDefaultUserDetails();
    this.setUpEditedUserFormControl();
  }

  get formControl() {
    return this.userForm.controls;
  }

  public onSubmit() {
    this.submitted = false;

    if (this.userForm.invalid) {
      return;
    }
    const updatedUser: UserTO = {
      ...this.obaUser,
      login: this.userForm.get('username').value,
      email: this.userForm.get('email').value,
      pin: this.userForm.get('pin').value,
    };
    this.currentUser
      .updateUserDetails(updatedUser)
      .subscribe(() => this.setDefaultUserDetails());
    this.shareDataService.updateUserDetails(updatedUser);
    this.infoService.openFeedback('User details was successfully updated!', {
      severity: 'info',
    });
    this.router.navigate(['/accounts']);
    console.log('updatedUser', updatedUser);
  }

  setUpEditedUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.email, Validators.required]],
      pin: ['', [Validators.required]],
    });
  }

  setDefaultUserDetails() {
    this.currentUserService.getCurrentUser().subscribe((data) => {
      console.log('data', data);
      this.obaUser = data.body;
      this.userForm.setValue({
        username: this.obaUser.login,
        email: this.obaUser.email,
        pin: this.obaUser.pin,
      });
    });
  }
}
