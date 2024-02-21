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

import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { environment } from '@environment/environment';
import { TppManagementService } from './tpp-management.service';

describe('TppManagementService', () => {
  let httpMock: HttpTestingController;
  let tppService: TppManagementService;
  const url = `${environment.tppAdminBackend}`;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TppManagementService],
    });
    tppService = TestBed.inject(TppManagementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should delete the Tpp user', () => {
    tppService.deleteSelf().subscribe();
    const req = httpMock.expectOne(url + '/self');
    expect(req.request.method).toBe('DELETE');
    httpMock.verify();
  });

  it('should delete the accountTransations ', () => {
    tppService.deleteAccountTransactions('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + /transactions/ + 'accountId');
    expect(req.request.url).toBe(url + /transactions/ + 'accountId');
    expect(req.request.method).toBe('DELETE');
    req.flush('accountId');
    httpMock.verify();
  });
});
