import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  constructor() {}

  clear() {
    localStorage.clear();
  }

  get(key: string): any {
    if (key !== undefined) {
      return JSON.parse(localStorage.getItem(key));
    }
  }

  remove(key: string): void {
    localStorage.removeItem(key);
  }

  set(key: string, value: any): void {
    localStorage.setItem(key, JSON.stringify(value));
  }
}
