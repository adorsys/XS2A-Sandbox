import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  constructor() {}

  clear() {
    return localStorage.clear();
  }

  get(key: string): any {
    if (key !== undefined) {
      return JSON.parse(localStorage.getItem(key));
    }
  }

  remove(key: string) {
    return localStorage.removeItem(key);
  }

  set(key: string, value: any) {
    return localStorage.setItem(key, JSON.stringify(value));
  }
}
