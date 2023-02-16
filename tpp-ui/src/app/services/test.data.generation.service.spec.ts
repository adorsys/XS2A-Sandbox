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

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestDataGenerationService } from './test.data.generation.service';
import { environment } from '../../environments/environment';

describe('TestDataGenerationService', () => {
  let httpMock: HttpTestingController;
  let testDataGenerationService: TestDataGenerationService;
  const url = `${environment.tppBackend}`;
  const userBranch = '';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TestDataGenerationService],
    });
    testDataGenerationService = TestBed.inject(TestDataGenerationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(testDataGenerationService).toBeTruthy();
  });

  it('should get a generate Iban', () => {
    testDataGenerationService.generateIban(userBranch).subscribe();
    const req = httpMock.expectOne(url + '/data/generate/iban?tppId=');
    expect(req.request.method).toBe('GET');
  });

  it('should generate the example Test Data', () => {
    testDataGenerationService.generateExampleTestData('accountId').subscribe((data: any) => {
      expect(data).toBe('accountId');
    });
    const req = httpMock.expectOne(url + 'accountId');
    expect(req.request.method).toBe('GET');
    req.flush('accountId');
    httpMock.verify();
  });

  it('should generate the Test Data', () => {
    testDataGenerationService.generateTestData('EUR', true).subscribe();
    const req = httpMock.expectOne(url + '/data/generate/EUR?generatePayments=true');
    expect(req.request.method).toBe('GET');
  });
});
