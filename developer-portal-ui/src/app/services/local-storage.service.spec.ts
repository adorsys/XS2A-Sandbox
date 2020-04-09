import { TestBed } from '@angular/core/testing';

import { LocalStorageService } from './local-storage.service';

describe('LocalStorageService', () => {
  let service: LocalStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LocalStorageService],
    });

    service = TestBed.get(LocalStorageService);
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
