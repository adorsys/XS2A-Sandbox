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

import { PageNavigationService } from './page-navigation.service';

describe('PageNavigationService', () => {
  let service: PageNavigationService;
  let storage = {};
  let pageLink = 'link';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PageNavigationService],
    });

    service = TestBed.get(PageNavigationService);

    spyOn(sessionStorage, 'getItem').and.callFake((key: string): string => {
      return storage[key] || null;
    });

    spyOn(sessionStorage, 'setItem').and.callFake(
      (key: string, value: string): string => {
        return (storage[key] = value as string);
      }
    );
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return null for item that was not put in storage', () => {
    expect(service.getLastVisitedPage()).toBeNull();
  });

  it('should set and get previous page to Session Storage', () => {
    service.setLastVisitedPage(pageLink);
    expect(service.getLastVisitedPage()).toBe(pageLink);
  });
});
