import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

import {InfoService} from '../../../commons/info/info.service';
import {CustomizeService} from '../../../services/customize.service';
import {SettingsService} from '../../../services/settings.service';
import {TppManagementService} from "../../../services/tpp-management.service";
import {PageNavigationService} from '../../../services/page-navigation.service';
import {Location} from '@angular/common';

@Component({
  selector: 'app-create-admin',
  templateUrl: './admin-create.component.html',
  styleUrls: ['./admin-create.component.scss'],
})
export class AdminCreateComponent implements OnInit {
  public userForm: FormGroup;
  public submitted: boolean;
  public errorMessage: string;
  public admin: false;

  constructor(
    private infoService: InfoService,
    private router: Router,
    private formBuilder: FormBuilder,
    private settingsService: SettingsService,
    public customizeService: CustomizeService,
    private pageNavigationService: PageNavigationService,
    private tppManagementService: TppManagementService,
    public location: Location
) {
  }

  ngOnInit() {
    this.initializeRegisterForm();
  }

  get formControl() {
    return this.userForm.controls;
  }

  public onSubmit(): void {
    if (this.userForm.invalid) {
      this.submitted = true;
      return;
    }

    let message: string;
    const navigateUrl = '/admin/all';
    this.tppManagementService
      .createAdmin(this.userForm.value)
      .subscribe(() => {
        message = `New Admin has been successfully registered.`;
        this.navigateAndGiveFeedback(navigateUrl, message);
      });
  }

  public navigateAndGiveFeedback(navigateUrl: string, message: string) {
    this.router.navigate([navigateUrl]).then(() => {
      this.infoService.openFeedback(message);
    });
  }


  public initializeRegisterForm(): void {
    this.userForm = this.formBuilder.group({
      login: ['', Validators.required],
      email: [
        '',
        [
          Validators.required,
          Validators.pattern(
            new RegExp(
              /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            )
          ),
        ],
      ],
      pin: ['', Validators.required],
    });
    this.userForm.enable();
  }
}
