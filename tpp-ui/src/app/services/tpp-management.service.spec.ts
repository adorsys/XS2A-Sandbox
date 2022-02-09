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

import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { environment } from '../../environments/environment';
import { TppManagementService } from './tpp-management.service';

describe('TppService', () => {
  let httpMock: HttpTestingController;
  let tppService: TppManagementService;
  const url = `${environment.tppBackend}`;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TppManagementService],
    });
    tppService = TestBed.get(TppManagementService);
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should delete the Tpp user', () => {
    tppService.deleteSelf();
  });

  it('should delete the accountTransations ', () => {
    tppService.deleteAccountTransactions('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + /account/ + 'accountId');
    expect(req.request.method).toBe('DELETE');
    req.flush('accountId');
    httpMock.verify();
  });
});
