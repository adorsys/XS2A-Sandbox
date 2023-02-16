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

import { LocalStorageService } from './local-storage.service';

describe('LocalStorageService', () => {
  let service: LocalStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LocalStorageService],
    });

    service = TestBed.inject(LocalStorageService);
  });

  // --- snip ---
  // Mock localStorage
  beforeEach(() => {
    let store = {};

    spyOn(localStorage, 'getItem').and.callFake((key: string): string => {
      return store[key] || null;
    });
    spyOn(localStorage, 'removeItem').and.callFake((key: string): void => {
      delete store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake((key: string, value: string): string => {
      return (store[key] = value as string);
    });
    spyOn(localStorage, 'clear').and.callFake(() => {
      store = {};
    });
  });

  // --- snap ---

  it('should set an Item', () => {
    expect(service).toBeTruthy();
    // @ts-ignore
    expect(LocalStorageService.set('foo', 'bar')).toEqual('bar');
    expect(LocalStorageService.get('foo')).toBe('bar');
  });

  it('should return null for non existing items', () => {
    expect(LocalStorageService.get('foo')).toBeNull(); // null
  });

  it('should set and remove Item', () => {
    // @ts-ignore
    expect(LocalStorageService.set('foo', 'bar')).toEqual('bar');
    expect(LocalStorageService.remove('foo')).toBeUndefined();
    expect(LocalStorageService.get('foo')).toBeNull();
  });

  it('should clear the storage', () => {
    // @ts-ignore
    expect(LocalStorageService.set('foo', 'bar')).toEqual('bar');
    // @ts-ignore
    expect(LocalStorageService.set('bar', 'foo')).toEqual('foo');
    expect(LocalStorageService.clear()).toBeUndefined();
    expect(LocalStorageService.get('foo')).toBeNull();
    expect(LocalStorageService.get('bar')).toBeNull();
  });
});
