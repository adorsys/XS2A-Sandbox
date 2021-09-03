import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PageNavigationService {
  private lastVisitedPageKey = 'lastVisitedPage';

  constructor() {}

  setLastVisitedPage(lastVisitedPageUrl: string): void {
    sessionStorage.setItem(this.lastVisitedPageKey, lastVisitedPageUrl);
  }

  getLastVisitedPage(): string {
    return sessionStorage.getItem(this.lastVisitedPageKey);
  }
}
