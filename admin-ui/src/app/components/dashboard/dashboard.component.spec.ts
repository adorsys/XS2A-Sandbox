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
import { RouterTestingModule } from '@angular/router/testing';

import { DashboardComponent } from './dashboard.component';
import { NavbarComponent } from '@commons/navbar/navbar.component';
import { SidebarComponent } from '@commons/sidebar/sidebar.component';
import { IconModule } from '@commons/icon/icon.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoginComponent } from '../auth/login/login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'isLoggedIn',
    'logout',
  ]);

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes([
            { path: 'logout', component: LoginComponent },
          ]),
          HttpClientTestingModule,
          ReactiveFormsModule,
          IconModule,
        ],
        providers: [
          { provide: AuthService, useValue: authServiceSpy },
        ],
        declarations: [
          DashboardComponent,
          NavbarComponent,
          SidebarComponent,
          LoginComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    authServiceSpy.isLoggedIn.and.returnValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });
});
