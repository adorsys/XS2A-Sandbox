/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { Component, OnInit } from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { InfoService } from '@commons/info/info.service';
import { ScaMethods } from '../../../models/scaMethods';
import { TppManagementService } from '../../../services/tpp-management.service';
import { Observable, of, Subscriber } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { HttpMethod } from '../../../models/http-method';
import { SettingsService } from '../../../services/settings.service';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss'],
})
export class UserCreateComponent implements OnInit {
  public url =
    `${this.settingsService.settings.tppBackendBasePath}` + '/tpp/push/tan';
  public error: string;
  id: string;
  users: User[];
  dataSource: Observable<User[]>;
  methods: string[];
  httpMethods: string[];
  userForm: UntypedFormGroup;
  submitted: boolean;
  asyncSelected: string;
  typeaheadLoading: boolean;
  tppIds: string[] = [];

  constructor(
    private userService: UserService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private infoService: InfoService,
    private route: ActivatedRoute,
    private tppManagementService: TppManagementService,
    private settingsService: SettingsService
  ) {
    this.dataSource = new Observable((observer: Subscriber<string>) => {
      observer.next(this.asyncSelected);
    }).pipe(mergeMap((token: string) => this.getStatesAsObservable(token)));
  }

  get formControl() {
    return this.userForm.controls;
  }

  ngOnInit() {
    this.listUsers();
    this.setupUserFormControl();
    this.getMethodsValues();
    this.getTppIds();
  }

  public getStatesAsObservable(token: string): Observable<User[]> {
    const query = new RegExp(token, 'i');

    return of(
      this.users.filter((state: any) => {
        return query.test(state.branch);
      })
    );
  }

  public changeTypeaheadLoading(e: boolean): void {
    this.typeaheadLoading = e;
  }

  listUsers() {
    this.tppManagementService.getTpps(0, 500).subscribe((resp: any) => {
      this.users = resp.tpps;
    });
  }

  setupUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      scaUserData: this.formBuilder.array([this.initScaData()]),
      tppId: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      login: ['', [Validators.required, Validators.minLength(5)]],
      pin: ['', [Validators.required, Validators.minLength(5)]],
      userRoles: this.formBuilder.array(['CUSTOMER']),
    });
  }

  initScaData() {
    const emailValidators = [
      Validators.required,
      Validators.pattern(
        new RegExp(
          /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        )
      ),
    ];

    const scaData = this.formBuilder.group({
      scaMethod: ['', Validators.required],
      methodValue: [''],
      pushMethod: '',
      staticTan: [{ value: '', disabled: true }],
      usesStaticTan: [false],
    });

    scaData.get('usesStaticTan').valueChanges.subscribe((bool = true) => {
      if (bool) {
        scaData.get('staticTan').setValidators(Validators.required);
        scaData.get('methodValue').setValidators(Validators.required);
        scaData.get('staticTan').enable();
      } else {
        scaData.get('staticTan').clearValidators();
        scaData.get('staticTan').disable();
        scaData.get('staticTan').setValue('');
      }
      scaData.get('staticTan').updateValueAndValidity();
      scaData.get('methodValue').updateValueAndValidity();
    });

    scaData.get('staticTan').valueChanges.subscribe((value) => {
      if (value === ScaMethods.SMTP_OTP) {
        scaData.get('staticTan').setValidators(emailValidators);
      } else if (value === ScaMethods.MOBILE) {
        scaData
          .get('staticTan')
          .setValidators([
            Validators.required,
            Validators.pattern(
              new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/)
            ),
          ]);
      } else {
        scaData.get('scaMethod').setValidators([Validators.required]);
      }
    });

    scaData.get('scaMethod').valueChanges.subscribe((value) => {
      if (value === ScaMethods.SMTP_OTP) {
        scaData.get('methodValue').setValidators(emailValidators);
      } else if (value === ScaMethods.MOBILE) {
        scaData
          .get('methodValue')
          .setValidators([
            Validators.required,
            Validators.pattern(
              new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/)
            ),
          ]);
      } else if (value === ScaMethods.PUSH_OTP) {
        scaData.get('methodValue').clearValidators();
      } else {
        scaData.get('methodValue').setValidators([Validators.required]);
      }
      scaData.get('methodValue').updateValueAndValidity();
    });
    return scaData;
  }

  addScaDataItem() {
    const control = <UntypedFormArray>this.userForm.controls['scaUserData'];
    control.push(this.initScaData());
  }

  removeScaDataItem(i: number) {
    const control = <UntypedFormArray>this.userForm.controls['scaUserData'];
    control.removeAt(i);
  }

  updateValue() {
    const body = this.userForm.value as User;
    body.scaUserData.forEach((d) => {
      if (d.scaMethod === 'PUSH_OTP') {
        if (d.pushMethod === '' || d.pushMethod === undefined) {
          d.pushMethod = 'POST';
        }
        if (d.methodValue === '' || d.methodValue === undefined) {
          d.methodValue = this.url;
        }
        d.methodValue = d.pushMethod + ',' + d.methodValue;
      }
      d.pushMethod = undefined;
    });
    return body;
  }

  onSubmit() {
    this.submitted = true;

    if (this.userForm.invalid) {
      console.log('Invalid form!');
      return;
    }

    const body = this.updateValue();
    this.tppManagementService
      .createUser(body, this.userForm.get('tppId').value)
      .subscribe({
        error: (e) => {
          this.infoService.openFeedback(
            e.error.message ? e.error.message : 'User creation failed!',
            {
              severity: 'info',
            }
          );
        },
        complete: () => {
          this.infoService.openFeedback('User was successfully created!', {
            severity: 'info',
          });
          this.router.navigate(['/users/all']);
        },
      });
  }

  getMethodsValues() {
    this.methods = Object.keys(ScaMethods);
    this.httpMethods = Object.keys(HttpMethod);
  }

  getTppIds() {
    this.tppManagementService.getTpps(0, 500).subscribe((resp: any) => {
      resp.tpps?.map((tpp: any) => {
        this.tppIds.push(tpp.id)
      });
    });
  }

  onCancel() {
    this.router.navigate(['/users/all']);
  }
}
