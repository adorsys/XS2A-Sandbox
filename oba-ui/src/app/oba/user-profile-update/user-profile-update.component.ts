import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { Router } from '@angular/router';
import { UserTO } from '../../api/models/user-to';

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
    private onlineBankingService: OnlineBankingService,
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
      login: this.userForm.get('login').value,
      email: this.userForm.get('email').value,
      pin: this.userForm.get('pin').value,
    };
  }

  setUpEditedUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.email, Validators.required]],
      pin: ['', [Validators.required]],
    });
  }

  setDefaultUserDetails() {
    this.onlineBankingService.getCurrentUser().subscribe((data) => {
      this.obaUser = data.body;
      this.userForm.setValue({
        username: this.obaUser.login,
        email: this.obaUser.email,
        pin: this.obaUser.pin,
      });
    });
  }
}
