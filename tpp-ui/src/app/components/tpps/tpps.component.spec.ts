/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { TppsComponent } from './tpps.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InfoService } from '../../commons/info/info.service';
import { AccountService } from '../../services/account.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PageNavigationService } from '../../services/page-navigation.service';
import { CountryService } from '../../services/country.service';
import { TppUserService } from '../../services/tpp.user.service';
import { OverlayModule } from '@angular/cdk/overlay';
import { of } from 'rxjs';

describe('TppsComponent', () => {
  let component: TppsComponent;
  let fixture: ComponentFixture<TppsComponent>;

  const mockRoute = {
    params: of({ id: '12345' }),
    queryParams: of({}),
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, OverlayModule],
        providers: [
          InfoService,
          AccountService,
          TppManagementService,
          TppUserService,
          PageNavigationService,
          CountryService,
          { provide: Router, useValue: {} },
          { provide: ActivatedRoute, useValue: mockRoute },
        ],
        declarations: [TppsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TppsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
