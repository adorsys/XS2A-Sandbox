import { TestBed } from '@angular/core/testing';

import { PageNavigationService } from './page-navigation.service';

describe('PageNavigationService', () => {
  let service: PageNavigationService;
  const storage = {};
  const pageLink = 'link';

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
