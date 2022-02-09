/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UserProfileUpdateComponent } from './user-profile-update.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { OnlineBankingAccountInformationService } from '../../api/services';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { InfoService } from '../../common/info/info.service';
import { OverlayModule } from '@angular/cdk/overlay';

describe('UserProfileEditComponent', () => {
  let component: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;
  let mockObaService: OnlineBankingService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [UserProfileUpdateComponent],
        imports: [ReactiveFormsModule, RouterTestingModule, OverlayModule],
        providers: [OnlineBankingAccountInformationService, InfoService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    component = fixture.componentInstance;
    mockObaService = TestBed.inject(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('validate setupUserFormControl method', () => {
    component.setUpEditedUserFormControl();
    expect(component.userForm).toBeDefined();
  });

  it('validate onSubmit method', () => {
    component.onSubmit();
    expect(component.userForm.valid).toBeFalsy();
  });

  it('validate formControl method', () => {
    expect(component.formControl).toEqual(component.userForm.controls);
  });
});
